/*
 * Copyright 2017 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.storyweb.core.metrics.spc

import groovy.util.logging.Slf4j
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.activity.ActivityRepository
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.annotation.AnnotationRespository
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.event.EventRepository
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.publisher.core.task.TaskRepository
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowBandGenerator
import org.openmastery.publisher.ideaflow.timeline.IdleTimeProcessor
import org.openmastery.publisher.ideaflow.timeline.RelativeTimeProcessor
import org.openmastery.time.TimeConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.sql.Timestamp

@Slf4j
@Component
class TaskDataGenerator {


	@Autowired
	IdeaFlowBandGenerator ideaFlowBandGenerator

	@Autowired
	IdleTimeProcessor idleTimeProcessor

	@Autowired
	RelativeTimeProcessor relativeTimeProcessor

	@Autowired
	EventRepository eventRepository

	@Autowired
	ActivityRepository activityRepository

	@Autowired
	AnnotationRespository annotationRespository

	@Autowired
	TaskRepository taskRepository


	EntityMapper entityMapper = new EntityMapper()

	List<TaskData> generate(long userId, LocalDate startDate, LocalDate endDate) {

		Timestamp startTimestamp = toBeginningOfDayTimestamp(startDate)
		Timestamp endTimestamp = toEndOfDayTimestamp(endDate)

		log.debug("generateTaskData from " + startTimestamp + ":" + endTimestamp)

		Set<Long> collectedTaskIds = new HashSet<>();

		List<Event> eventsWithinRange = findEventsWithinRange(collectedTaskIds, userId, startTimestamp, endTimestamp)
		List<IdleTimeBandModel> idleBands = findIdleBandsWithinRange(collectedTaskIds, userId, startTimestamp, endTimestamp)
		List<ExecutionEvent> executionEvents = findExecutionEventsWithinRange(collectedTaskIds, userId, startTimestamp, endTimestamp)
		List<FaqAnnotationEntity> faqAnnotations = findFaqsWithinRange(collectedTaskIds, userId, startTimestamp, endTimestamp)
		List<Task> tasks = findTasksWithIds(collectedTaskIds, userId)

		List<TaskData> taskDataList = splitIntoTasks(tasks, eventsWithinRange, idleBands, executionEvents, faqAnnotations)

		taskDataList.each { TaskData taskData ->


			taskData.troubleshootingBands = ideaFlowBandGenerator.generateTroubleshootingBands(taskData.events)
			taskData.idleBands += idleTimeProcessor.generateIdleTimeBandsFromDeativationEvents(taskData.events)

			idleTimeProcessor.collapseIdleTime(taskData.troubleshootingBands, taskData.idleBands)

			List<Positionable> positionables = taskData.getSortedPositionables()

			relativeTimeProcessor.computeRelativeTime(positionables)

			if (positionables.size() > 0) {
				Positionable firstEvent = positionables.first()
				taskData.setStart(firstEvent.position)

				Positionable lastEvent = positionables.last()
				taskData.setEnd(lastEvent.position)
				taskData.setDurationInSeconds(lastEvent.relativePositionInSeconds)
			}
		}
		return taskDataList
	}


	private List<TaskData> splitIntoTasks(List<Task> tasks, List<Event> events, List<IdleTimeBandModel> idleBands,
										  List<ExecutionEvent> executionEvents, List<FaqAnnotationEntity> faqs) {
		Map<Long, TaskData> taskDataMap = [:]

		//Events get to determine whether or not a task exists in scope.
		events.each { Event event ->
			TaskData data = taskDataMap.get(event.taskId)
			if (data == null) {
				data = new TaskData(event.taskId)
				taskDataMap.put(event.taskId, data)
			}
			data.addEvent(event)
		}

		tasks.each { Task task ->
			TaskData data = taskDataMap.get(task.id)
			if (data != null) {
				data.setTask(task)
			}
		}

		idleBands.each { IdleTimeBandModel idleBand ->
			TaskData data = taskDataMap.get(idleBand.taskId)
			if (data != null) {
				data.addIdle(idleBand)
			}
		}

		executionEvents.each { ExecutionEvent executionEvent ->
			TaskData data = taskDataMap.get(executionEvent.taskId)
			if (data != null) {
				data.addExecutionEvent(executionEvent)
			}
		}

		faqs.each { FaqAnnotationEntity faqEntity ->
			TaskData data = taskDataMap.get(faqEntity.taskId)
			if (data != null) {
				data.addFaq(faqEntity)
			}
		}

		return taskDataMap.values().toList()
	}

	private Timestamp toBeginningOfDayTimestamp(LocalDate localDate) {
		LocalDateTime beginningOfDay = localDate.toLocalDateTime(new LocalTime(0, 0, 0))
		return TimeConverter.toSqlTimestamp(beginningOfDay)
	}

	private Timestamp toEndOfDayTimestamp(LocalDate localDate) {
		LocalDateTime endOfDay = localDate.toLocalDateTime(new LocalTime(23, 59, 59))
		return TimeConverter.toSqlTimestamp(endOfDay)
	}

	private List<IdleTimeBandModel> findIdleBandsWithinRange(Set<Long> taskIds, Long userId, Timestamp startTimestamp, Timestamp endTimestamp) {
		List<IdleActivityEntity> idleActivities = activityRepository.findIdlesWithinRange(userId, startTimestamp, endTimestamp)
		taskIds.addAll(idleActivities.collect { it.taskId })
		return entityMapper.mapList(idleActivities, IdleTimeBandModel)
	}

	private List<Event> findEventsWithinRange(Set<Long> taskIds, Long userId, Timestamp startTimestamp, Timestamp endTimestamp) {
		List<EventEntity> eventEntities = eventRepository.findEventsWithinsRange(userId, startTimestamp, endTimestamp)
		taskIds.addAll(eventEntities.collect { it.taskId })
		return entityMapper.mapList(eventEntities, Event)
	}

	private List<ExecutionEvent> findExecutionEventsWithinRange(Set<Long> taskIds, Long userId, Timestamp startTimestamp, Timestamp endTimestamp) {
		List<ExecutionActivityEntity> eventEntities = activityRepository.findExecutionActivityWithinRange(userId, startTimestamp, endTimestamp)
		List<ExecutionEvent> executionEvents = []
		eventEntities.each { ExecutionActivityEntity entity ->
			ExecutionEvent execution = entityMapper.mapIfNotNull(entity, ExecutionEvent)
			execution.failed = entity.exitCode != 0
			execution.durationInSeconds = TimeConverter.between(entity.start, entity.end).standardSeconds
			executionEvents.add(execution)
			taskIds.add(execution.taskId)
		}

		return executionEvents
	}

	private List<Task> findTasksWithIds(Set<Long> taskIds, Long userId) {
		if (taskIds.size() > 0) {
			List<TaskEntity> taskEntities = taskRepository.findTasksWithIds(userId, taskIds.toList())
			return entityMapper.mapList(taskEntities, Task)
		} else {
			return []
		}
	}

	private List<FaqAnnotationEntity> findFaqsWithinRange(Set<Long> taskIds, Long userId, Timestamp startTimestamp, Timestamp endTimestamp) {
		List<FaqAnnotationEntity> faqEntities = annotationRespository.findFaqsWithinRange(userId, startTimestamp, endTimestamp)
		taskIds.addAll(faqEntities.collect { it.taskId })
		return faqEntities
	}


}

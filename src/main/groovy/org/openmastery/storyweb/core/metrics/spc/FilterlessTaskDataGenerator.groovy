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
import org.openmastery.mapper.ValueObjectMapper
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.activity.ActivityRepository
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Slf4j
@Component
class FilterlessTaskDataGenerator {

	//TODO total hack need to delete this whole class...


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


	ValueObjectMapper entityMapper = new ValueObjectMapper()


	List<TaskData> generate(long userId) {

		Set<Long> collectedTaskIds = new HashSet<>();

		List<Event> eventsWithinRange = findEventsWithinRange(collectedTaskIds, userId)
		List<IdleTimeBandModel> idleBands = findIdleBandsWithinRange(collectedTaskIds, userId)
		List<FaqAnnotationEntity> faqAnnotations = findFaqsWithinRange(collectedTaskIds, userId)
		List<Task> tasks = findTasksWithIds(collectedTaskIds, userId)

		List<TaskData> taskDataList = splitIntoTasks(tasks, eventsWithinRange, idleBands, faqAnnotations)

		taskDataList.each { TaskData taskData ->


			taskData.troubleshootingBands = ideaFlowBandGenerator.generateTroubleshootingBands(taskData.events)
			taskData.idleBands += idleTimeProcessor.generateIdleTimeBandsFromDeativationEvents(taskData.events)

			idleTimeProcessor.collapseIdleTime(taskData.troubleshootingBands, taskData.idleBands)

			List<Positionable> positionables = taskData.getSortedPositionables()

			relativeTimeProcessor.computeRelativeTime(positionables)

			if (positionables.size() > 0) {
				Positionable firstEvent = positionables.first()
				taskData.start = firstEvent.position

				Positionable lastEvent = positionables.last()
				taskData.end = lastEvent.position
				taskData.durationInSeconds = lastEvent.relativePositionInSeconds
			}
		}
		return taskDataList
	}


	private List<TaskData> splitIntoTasks(List<Task> tasks, List<Event> events, List<IdleTimeBandModel> idleBands,
										  List<FaqAnnotationEntity> faqs) {
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

		faqs.each { FaqAnnotationEntity faqEntity ->
			TaskData data = taskDataMap.get(faqEntity.taskId)
			if (data != null) {
				data.addFaq(faqEntity)
			}
		}

		return taskDataMap.values().toList().findAll { it.task != null}
	}

	private List<IdleTimeBandModel> findIdleBandsWithinRange(Set<Long> taskIds, Long userId) {
		List<IdleActivityEntity> idleActivities = activityRepository.findAllIdlesForUser(userId)
		taskIds.addAll(idleActivities.collect { it.taskId })
		return entityMapper.mapList(idleActivities, IdleTimeBandModel)
	}

	private List<Event> findEventsWithinRange(Set<Long> taskIds, Long userId) {
		List<EventEntity> eventEntities = eventRepository.findAllByUser(userId)
		taskIds.addAll(eventEntities.collect { it.taskId })
		return eventEntities.collect { EventEntity entity ->
			Event event = entityMapper.mapIfNotNull(entity, Event)
			event.description = entity.comment
			return event
		}
	}

	private List<Task> findTasksWithIds(Set<Long> taskIds, Long userId) {
		if (taskIds.size() > 0) {
			List<TaskEntity> taskEntities = taskRepository.findTasksWithIds(userId, taskIds.toList())
			return entityMapper.mapList(taskEntities, Task)
		} else {
			return []
		}
	}

	private List<FaqAnnotationEntity> findFaqsWithinRange(Set<Long> taskIds, Long userId) {
		List<FaqAnnotationEntity> faqEntities = annotationRespository.findAllFaqsByUser(userId)
		taskIds.addAll(faqEntities.collect { it.taskId })
		return faqEntities
	}


}

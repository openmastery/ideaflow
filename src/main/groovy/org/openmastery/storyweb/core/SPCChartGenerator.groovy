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
package org.openmastery.storyweb.core

import groovy.util.logging.Slf4j
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.annotation.FAQAnnotation
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.journey.DiscoveryCycle
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.activity.ActivityRepository
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.annotation.AnnotationRespository
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.annotation.SnippetAnnotationEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.event.EventRepository
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.publisher.core.task.TaskRepository
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.ideaflow.timeline.DiscoveryCycleTimeline
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowBandGenerator
import org.openmastery.publisher.ideaflow.timeline.IdleTimeProcessor
import org.openmastery.publisher.ideaflow.timeline.JourneySetTimeline
import org.openmastery.publisher.ideaflow.timeline.JourneyTimeline
import org.openmastery.publisher.ideaflow.timeline.RelativeTimeProcessor
import org.openmastery.publisher.ideaflow.timeline.TroubleshootingJourneyGenerator
import org.openmastery.publisher.metrics.subtask.MetricsService
import org.openmastery.publisher.security.InvocationContext
import org.openmastery.storyweb.api.ExplodableGraphPoint
import org.openmastery.storyweb.api.SPCChart
import org.openmastery.time.TimeConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.sql.Timestamp

@Slf4j
@Component
class SPCChartGenerator {


	@Autowired
	IdeaFlowBandGenerator ideaFlowBandGenerator

	@Autowired
	TroubleshootingJourneyGenerator troubleshootingJourneyGenerator

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

	@Autowired
	MetricsService metricsService

	EntityMapper entityMapper = new EntityMapper()

	SPCChart generateChart(Long userId, LocalDate startDate, LocalDate endDate) {

		List<TaskData> taskDataList = generateTaskData(userId, startDate, endDate)
		List<ExplodableGraphPoint> graphPoints = taskDataList.collect { TaskData taskData ->
			taskData.intoGraphPoint()
		}

		SPCChart chart = new SPCChart()
		chart.addGraphPoints( graphPoints )
		chart.metricThresholds = metricsService.getDefaultMetricsThresholds()

		return chart;
	}

	List<TaskData> generateTaskData(Long userId, LocalDate startDate, LocalDate endDate) {

		Timestamp startTimestamp = toBeginningOfDayTimestamp(startDate)
		Timestamp endTimestamp = toEndOfDayTimestamp(endDate)

		log.debug("generateTaskData from " +startTimestamp + ":" + endTimestamp)

		List<Event> eventsWithinRange = findEventsWithinRange(userId, startTimestamp, endTimestamp)
		List<IdleTimeBandModel> idleBands = findIdleBandsWithinRange(userId, startTimestamp, endTimestamp)
		List<ExecutionEvent> executionEvents = findExecutionEventsWithinRange(userId, startTimestamp, endTimestamp)
		List<Task> tasks = findTasksWithinRange(userId, startTimestamp, endTimestamp)
		List<FaqAnnotationEntity> faqAnnotations = findFaqsWithinRange(userId, startTimestamp, endTimestamp)

		List<TaskData> taskDataList = splitIntoTasks(tasks, eventsWithinRange, idleBands, executionEvents, faqAnnotations)

		taskDataList.each { TaskData taskData ->
			taskData.troubleshootingBands = ideaFlowBandGenerator.generateTroubleshootingBands(taskData.events)

			idleTimeProcessor.collapseIdleTime(taskData.troubleshootingBands, taskData.idleBands, taskData.events)

			relativeTimeProcessor.computeRelativeTime(taskData.getAllPositionables())


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

	private List<IdleTimeBandModel> findIdleBandsWithinRange(Long userId, Timestamp startTimestamp, Timestamp endTimestamp) {
		List<IdleActivityEntity> idleActivities = activityRepository.findIdlesWithinRange(userId, startTimestamp, endTimestamp)
		return entityMapper.mapList(idleActivities, IdleTimeBandModel)
	}

	private List<Event> findEventsWithinRange(Long userId, Timestamp startTimestamp, Timestamp endTimestamp) {
		List<EventEntity> eventEntities = eventRepository.findEventsWithinsRange(userId, startTimestamp, endTimestamp)
		return entityMapper.mapList(eventEntities, Event)
	}

	private List<ExecutionEvent> findExecutionEventsWithinRange(Long userId, Timestamp startTimestamp, Timestamp endTimestamp) {
		List<ExecutionActivityEntity> eventEntities = activityRepository.findExecutionActivityWithinRange(userId, startTimestamp, endTimestamp)
		List<ExecutionEvent> executionEvents = []
		eventEntities.each { ExecutionActivityEntity entity ->
			ExecutionEvent execution = entityMapper.mapIfNotNull(entity, ExecutionEvent)
			execution.failed = entity.exitCode != 0
			execution.durationInSeconds = TimeConverter.between(entity.start, entity.end).standardSeconds
			executionEvents.add(execution)
		}
		return executionEvents
	}

	private List<Task> findTasksWithinRange(Long userId, Timestamp startTimestamp, Timestamp endTimestamp) {
		List<TaskEntity> taskEntities = taskRepository.findTasksWithinRange(userId, startTimestamp, endTimestamp)
		return entityMapper.mapList(taskEntities, Task)
	}

	private List<FaqAnnotationEntity> findFaqsWithinRange(Long userId, Timestamp startTimestamp, Timestamp endTimestamp) {
		return annotationRespository.findFaqsWithinRange(userId, startTimestamp, endTimestamp)
	}


	private class TaskData {

		long taskId
		Task task

		List<Event> events = []
		List<IdleTimeBandModel> idleBands = []
		List<ExecutionEvent> executionEvents = []
		List<FaqAnnotationEntity> faqAnnotations = []
		List<IdeaFlowBandModel> troubleshootingBands

		List<TroubleshootingJourney> journeys

		TaskData(long taskId) {
			this.taskId = taskId
		}

		void setTask(Task task) {
			this.task = task
		}

		void addEvent(Event event) {
			events.add(event)
		}

		void addIdle(IdleTimeBandModel idleBand) {
			idleBands.add(idleBand)
		}

		void addFaq(FaqAnnotationEntity faq) {
			faqAnnotations.add(faq)
		}

		void addExecutionEvent(ExecutionEvent executionEvent) {
			executionEvents.add(executionEvent)
		}

		List<Positionable> getAllPositionables() {
			List<Positionable> positionables = []
			positionables.addAll(events)
			positionables.addAll(idleBands)
			positionables.addAll(executionEvents)
			positionables.addAll(troubleshootingBands)
			return positionables
		}

		LocalDateTime getPosition() {
			if (events.size() > 0) {
				return events.get(0).position
			} else {
				return task.creationDate
			}
		}

		ExplodableGraphPoint intoGraphPoint() {

			List<IdeaFlowBand> ideaFlowBands = entityMapper.mapList(troubleshootingBands, IdeaFlowBand.class)
			journeys = troubleshootingJourneyGenerator.splitIntoJourneys(events, ideaFlowBands, executionEvents)

			troubleshootingJourneyGenerator.annotateJourneys(journeys, faqAnnotations, [])

			ExplodableGraphPoint graphPoint = new ExplodableGraphPoint()

			List<ExplodableGraphPoint> childPoints = []
			journeys.each { TroubleshootingJourney journey ->
				graphPoint.painTags.addAll(journey.painTags)
				graphPoint.contextTags.addAll(journey.contextTags)
				graphPoint.durationInSeconds.incrementBy(journey.getDurationInSeconds())
				childPoints.add( journey.toGraphPoint() )
			}

			graphPoint.frequency = journeys.size()
			graphPoint.relativePath = "/task/"+taskId
			graphPoint.description = task?.description
			graphPoint.typeName = Task.class.simpleName
			graphPoint.position = getPosition()
			graphPoint.explodableGraphPoints = childPoints

			return graphPoint
		}

	}


}

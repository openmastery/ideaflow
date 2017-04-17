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
package org.openmastery.publisher.ideaflow

import com.bancvue.rest.exception.NotFoundException
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.Haystack
import org.openmastery.publisher.api.ideaflow.IdeaFlowSubtaskTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.ideaflow.SubtaskTimelineOverview
import org.openmastery.publisher.api.ideaflow.TaskTimelineOverview
import org.openmastery.publisher.api.ideaflow.TaskTimelineWithAllSubtasks
import org.openmastery.publisher.api.journey.IdeaFlowStory
import org.openmastery.publisher.api.journey.StoryElement
import org.openmastery.publisher.api.journey.SubtaskStory
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.TaskService
import org.openmastery.publisher.core.activity.BlockActivityEntity
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.jpa.LocalDateTimeConverter
import org.openmastery.publisher.core.timeline.TimelineGenerator
import org.openmastery.publisher.ideaflow.story.AnnotationDecorator
import org.openmastery.publisher.ideaflow.story.CapacityDistributionDecorator
import org.openmastery.publisher.ideaflow.story.HaystackListGenerator
import org.openmastery.publisher.ideaflow.story.IdeaFlowStoryGenerator
import org.openmastery.publisher.ideaflow.story.MetricsDecorator
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTaskTimelineGenerator
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineSplitter
import org.openmastery.publisher.ideaflow.timeline.RelativeTimeProcessor
import org.openmastery.storyweb.core.MetricsService
import org.openmastery.storyweb.core.metrics.spc.MetricSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.LocalDate
import java.time.LocalDateTime

@Component
class IdeaFlowService {

	private static final List<EventType> TASK_TIMELINE_EVENTS_TO_RETAIN = [
			EventType.SUBTASK,
			EventType.CALENDAR,
			EventType.WTF,
			EventType.AWESOME,
			EventType.DISTRACTION
	]
	private static final List<EventType> SUBTASK_TIMELINE_EVENTS_TO_RETAIN = [
			EventType.NOTE,
			EventType.WTF,
			EventType.AWESOME,
			EventType.CALENDAR,
			EventType.DISTRACTION
	]

	@Autowired
	private IdeaFlowPersistenceService persistenceService;

	@Autowired
	private IdeaFlowTaskTimelineGenerator.Factory timelineGeneratorFactory
	@Autowired
	private TaskService taskService

	@Autowired
	private AnnotationDecorator annotationDecorator

	@Autowired //TODO this has a storyweb dependency
	private MetricsService metricsService

	private MetricsDecorator metricsDecorator = new MetricsDecorator()

	private IdeaFlowStoryGenerator storyGenerator = new IdeaFlowStoryGenerator()
	private CapacityDistributionDecorator capacityDecorator = new CapacityDistributionDecorator()

	/**
	 * Generates a timeline and the corresponding story structure for a given task
	 * @param taskId
	 * @return TaskTimelineOverview
	 */
	TaskTimelineOverview generateTimelineOverviewForTask(Long taskId) {
		Task task = taskService.findTaskWithId(taskId)
		IdeaFlowTaskTimeline timeline = generateTaskTimeline(task)

		if (timeline == null) {
			return TaskTimelineOverview.builder().task(task).build()
		}
		IdeaFlowStory story = storyGenerator.generateIdeaFlowStory(timeline)
		capacityDecorator.decorateStoryWithCapacityDistributions(story)

		MetricSet metricSet = metricsService.generateMetricsForTask(story)
		metricsDecorator.decorateStoryWithMetrics(story, metricSet)

		cascadePainAndContextTags(story)
		pruneToSubtaskDepth(story)

		if (timeline != null) {
			List<Event> filteredEvents = filterEventsByType(timeline.events, TASK_TIMELINE_EVENTS_TO_RETAIN)
			timeline.setEvents(filteredEvents)
		}

		TaskTimelineOverview.builder()
				.task(task)
				.timeline(timeline)
				.ideaFlowStory(story)
				.build()
	}

	private void cascadePainAndContextTags(IdeaFlowStory ideaFlowStory) {
		forcePushContextTagsToChildren(ideaFlowStory, ideaFlowStory.contextTags)
		forceBubbleUpAllPain(ideaFlowStory)
	}

	private void forcePushContextTagsToChildren(StoryElement storyElement, Set<String> contextTags) {
		storyElement.contextTags.addAll(contextTags)
		for (StoryElement childElement: storyElement.childStoryElements) {
			forcePushContextTagsToChildren(childElement, storyElement.contextTags)
		}
	}

	private void forceBubbleUpAllPain(StoryElement storyElement) {
		Set<String> allPain = new HashSet<String>();
		for (StoryElement childPoint : storyElement.childStoryElements) {
			forceBubbleUpAllPain(childPoint);
			allPain.addAll(childPoint.painTags);
		}
		storyElement.painTags.addAll(allPain);
	}



	private void pruneToSubtaskDepth(IdeaFlowStory ideaFlowStory) {
		ideaFlowStory.subtasks.each { SubtaskStory subtaskStory ->
			subtaskStory.progressTicks = []
			subtaskStory.troubleshootingJourneys = []
		}
	}
/**
	 * Generates a timeline and the corresponding story structure for a given subtask
	 * Stories are broken down into significantly more detail with the addition of
	 * progressTicks and troubleshooting journeys
	 *
	 * @param taskId
	 * @param subtaskId
	 * @return SubtaskTimelineOverview
	 */

	SubtaskTimelineOverview generateTimelineOverviewForSubtask(Long taskId, Long subtaskId) {
		Task task = taskService.findTaskWithId(taskId)
		IdeaFlowTaskTimeline taskTimeline = generateTaskTimeline(task);

		IdeaFlowSubtaskTimeline subtaskTimeline = generateSubtaskTimeline(taskTimeline, subtaskId)

		IdeaFlowStory story = storyGenerator.generateIdeaFlowStoryScopedToSubtask(taskTimeline, subtaskTimeline)

		capacityDecorator.decorateStoryWithCapacityDistributions(story)
		annotationDecorator.decorateStoryWithAnnotations(story)
		MetricSet metricSet = metricsService.generateMetricsForTask(story)
		metricsDecorator.decorateStoryWithMetrics(story, metricSet)


		if (subtaskTimeline != null) {
			List<Event> filteredEvents = filterEventsByType(subtaskTimeline.events, SUBTASK_TIMELINE_EVENTS_TO_RETAIN)
			subtaskTimeline.setEvents(filteredEvents)
		}

		SubtaskTimelineOverview.builder()
				.subtask(subtaskTimeline.subtask)
				.timeline(subtaskTimeline)
				.ideaFlowStory(story)
				.build()
	}


	private List<Event> filterEventsByType(List<Event> events, List<EventType> eventTypesToRetain) {
		events.findAll { Event event ->
			eventTypesToRetain.contains(event.type)
		}
	}


	private IdeaFlowSubtaskTimeline generateSubtaskTimeline(IdeaFlowTaskTimeline taskTimeline, long subtaskId) {
		List<IdeaFlowSubtaskTimeline> subtaskTimelineList = splitTimelineBySubtaskEvents(taskTimeline)

		IdeaFlowSubtaskTimeline subtaskTimeline = subtaskTimelineList.find { IdeaFlowSubtaskTimeline subtaskTimeline ->
			subtaskTimeline.subtask.id == subtaskId
		}
		subtaskTimeline
	}

	private List<IdeaFlowSubtaskTimeline> splitTimelineBySubtaskEvents(IdeaFlowTaskTimeline timeline) {
		return new IdeaFlowTimelineSplitter()
				.timeline(timeline)
				.splitBySubtaskEvents()
	}

	private IdeaFlowTaskTimeline generateTaskTimeline(Task task) {

		List<ModificationActivityEntity> modifications = persistenceService.getModificationActivityList(task.id)
		List<EventEntity> events = persistenceService.getEventList(task.id)
		List<ExecutionActivityEntity> executions = persistenceService.getExecutionActivityList(task.id)
		List<BlockActivityEntity> blocks = persistenceService.getBlockActivityList(task.id)
		List<IdleActivityEntity> idleActivities = persistenceService.getIdleActivityList(task.id)

		IdeaFlowTaskTimeline timeline = timelineGeneratorFactory.create()
				.task(task)
				.modificationActivities(modifications)
				.events(events)
				.executionActivities(executions)
				.blockActivities(blocks)
				.idleActivities(idleActivities)
				.generate()

		return timeline
	}

	TaskTimelineWithAllSubtasks generateTimelineWithAllSubtasks(Long taskId) {
		Task task = taskService.findTaskWithId(taskId)
		IdeaFlowTaskTimeline taskTimeline = generateTaskTimeline(task);
		if (taskTimeline == null) {
			throw new NotFoundException();
		}

		if (taskTimeline != null) {
			List<Event> filteredEvents = filterEventsByType(taskTimeline.events, TASK_TIMELINE_EVENTS_TO_RETAIN)
			taskTimeline.setEvents(filteredEvents)
		}

		List<IdeaFlowSubtaskTimeline> subtaskTimelines = splitTimelineBySubtaskEvents(taskTimeline)

		IdeaFlowStory story = storyGenerator.generateIdeaFlowStory(taskTimeline)

		capacityDecorator.decorateStoryWithCapacityDistributions(story)
		annotationDecorator.decorateStoryWithAnnotations(story)
		MetricSet metricSet = metricsService.generateMetricsForTask(story)
		metricsDecorator.decorateStoryWithMetrics(story, metricSet)

		cascadePainAndContextTags(story)

		List<Haystack> haystacks = generateHaystacks(task, taskTimeline.start)

		TaskTimelineWithAllSubtasks.builder()
				.task(task)
				.timeline(taskTimeline)
				.subtaskTimelines(subtaskTimelines)
				.haystacks(haystacks)
				.ideaFlowStory(story)
				.build()

	}


	// TODO: clean this up
	@Autowired
	RelativeTimeProcessor relativeTimeProcessor

	// TODO: taskStart should be calculated within the generator, not passed in
	private List<Haystack> generateHaystacks(Task task, LocalDateTime taskStart) {

		List<EventEntity> events = persistenceService.getEventList(task.id)
		List<ExecutionActivityEntity> executions = persistenceService.getExecutionActivityList(task.id)
		List<IdleActivityEntity> idleActivities = persistenceService.getIdleActivityList(task.id)
		List<ExternalActivityEntity> externalActivities = persistenceService.getExternalActivityList(task.id)
		List<EditorActivityEntity> editorActivities = persistenceService.getEditorActivityList(task.id)

		new HaystackListGenerator(relativeTimeProcessor)
				.idleActivities(idleActivities)
				.editorActivities(editorActivities)
				.externalActivities(externalActivities)
				.executionActivities(executions)
				.events(events)
				.taskStart(taskStart)
				.generate()
	}

}

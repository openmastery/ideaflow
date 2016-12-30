/*
 * Copyright 2016 New Iron Group, Inc.
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

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowSubtaskTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.ideaflow.SubtaskTimelineOverview
import org.openmastery.publisher.api.ideaflow.TaskTimelineOverview
import org.openmastery.publisher.api.journey.ProgressMilestone
import org.openmastery.publisher.api.journey.TroubleshootingJourney

import org.openmastery.publisher.api.metrics.SubtaskOverview
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.TaskService
import org.openmastery.publisher.core.activity.BlockActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.annotation.SnippetAnnotationEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTaskTimelineGenerator
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineSplitter
import org.openmastery.publisher.ideaflow.timeline.TroubleshootingJourneyGenerator
import org.openmastery.publisher.metrics.subtask.MetricsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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
	private TroubleshootingJourneyGenerator troubleshootingJourneyGenerator;
	@Autowired
	private TaskService taskService

	@Autowired
	private MetricsService metricsService



	TaskTimelineOverview generateTimelineOverviewForTask(Long taskId) {
		Task task = taskService.findTaskWithId(taskId)
		IdeaFlowTaskTimeline timeline = generateTaskTimeline(task);
		List<SubtaskOverview> subtaskTimelineMetrics = generateTimelineMetricsBySubtask(timeline);

		List<Event> filteredEvents = filterEventsByType(timeline.events, TASK_TIMELINE_EVENTS_TO_RETAIN)
		timeline.setEvents(filteredEvents)

		TaskTimelineOverview.builder()
				.task(task)
				.timeline(timeline)
				.subtaskOverviews(subtaskTimelineMetrics)
				.build()
	}

	private List<Event> filterEventsByType(List<Event> events, List<EventType> eventTypesToRetain) {
		events.findAll { Event event ->
			eventTypesToRetain.contains(event.type)
		}
	}

	SubtaskTimelineOverview generateTimelineOverviewForSubtask(Long taskId, Long subtaskId) {
		Task task = taskService.findTaskWithId(taskId)

		IdeaFlowSubtaskTimeline subtaskTimeline = generateSubtaskTimeline(task, subtaskId)
		List<Event> filteredEvents = filterEventsByType(subtaskTimeline.events, SUBTASK_TIMELINE_EVENTS_TO_RETAIN)
		subtaskTimeline.setEvents(filteredEvents)

		List<ProgressMilestone> progressMilestones = generateProgressMilestones(subtaskTimeline)
		List<TroubleshootingJourney> troubleshootingJourneys = troubleshootingJourneyGenerator.createFromTimeline(subtaskTimeline);

		List<FaqAnnotationEntity> faqs = persistenceService.getFaqAnnotationList(taskId)
		List<SnippetAnnotationEntity> snippets = persistenceService.getSnippetAnnotationList(taskId)
		troubleshootingJourneyGenerator.annotateJourneys(troubleshootingJourneys, faqs, snippets)

		distributeJourneysByMilestone(progressMilestones, troubleshootingJourneys)

		SubtaskOverview metrics = metricsService.generateSubtaskOverview(subtaskTimeline.subtask, subtaskTimeline)

		progressMilestones.each { ProgressMilestone milestone ->
			milestone.capacityDistribution = metricsService.calculateCapacityDistribution(subtaskTimeline, milestone)
		}

		SubtaskTimelineOverview.builder()
				.subtask(subtaskTimeline.subtask)
				.timeline(subtaskTimeline)
				.overview(metrics)
				.progressMilestones(progressMilestones)
				.build()
	}


	void distributeJourneysByMilestone(List<ProgressMilestone> progressMilestones, List<TroubleshootingJourney> troubleshootingJourneys) {
		progressMilestones.each { ProgressMilestone milestone ->
			troubleshootingJourneys.each { TroubleshootingJourney journey ->
				if (milestone.shouldContain(journey)) {
					milestone.addJourney(journey)
				}
			}
		}
	}

	List<ProgressMilestone> generateProgressMilestones(IdeaFlowSubtaskTimeline subtaskTimeline) {
		Long relativeStart = subtaskTimeline.relativeStart
		Long relativeEnd = subtaskTimeline.relativeEnd

		List<Event> progressNotes = subtaskTimeline.events.findAll { Event event ->
			event.type == EventType.NOTE
		}

		ProgressMilestone lastMilestone = null
		List<ProgressMilestone> progressMilestones = []

		Event defaultEvent = subtaskTimeline.subtask
		ProgressMilestone defaultMilestone = new ProgressMilestone(defaultEvent)
		defaultMilestone.durationInSeconds = relativeEnd - relativeStart
		progressMilestones.add(defaultMilestone)

		lastMilestone = defaultMilestone

		progressNotes.each { Event progressNote ->
			ProgressMilestone milestone = new ProgressMilestone(progressNote)
			progressMilestones.add(milestone)

			lastMilestone.durationInSeconds = progressNote.relativePositionInSeconds - lastMilestone.relativePositionInSeconds
			lastMilestone = milestone
		}

		lastMilestone.durationInSeconds = relativeEnd - lastMilestone.relativePositionInSeconds

		return progressMilestones
	}

	private IdeaFlowSubtaskTimeline generateSubtaskTimeline(Task task, long subtaskId) {
		IdeaFlowTaskTimeline timeline = generateTaskTimeline(task);
		List<IdeaFlowSubtaskTimeline> subtaskTimelineList = splitTimelineBySubtaskEvents(timeline)

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

	/**
	 * Generates the primary IdeaFlowTimeline that can be used for visualization,
	 * or input to metrics calculations.
	 *
	 * @param taskId
	 * @return IdeaFlowTimeline
	 */
	private IdeaFlowTaskTimeline generateTaskTimeline(Task task) {

		List<ModificationActivityEntity> modifications = persistenceService.getModificationActivityList(task.id)
		List<EventEntity> events = persistenceService.getEventList(task.id)
		List<ExecutionActivityEntity> executions = persistenceService.getExecutionActivityList(task.id)
		List<BlockActivityEntity> blocks = persistenceService.getBlockActivityList(task.id)
		List<IdleActivityEntity> idleActivities = persistenceService.getIdleActivityList(task.id)


		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimelineGenerator()
				.task(task)
				.modificationActivities(modifications)
				.events(events)
				.executionActivities(executions)
				.blockActivities(blocks)
				.idleActivities(idleActivities)
				.generate()

		return timeline
	}

	private List<SubtaskOverview> generateTimelineMetricsBySubtask(IdeaFlowTaskTimeline timeline) {
		List<IdeaFlowSubtaskTimeline> subtaskTimelineList = splitTimelineBySubtaskEvents(timeline)

		subtaskTimelineList.collect { IdeaFlowSubtaskTimeline subtaskTimeline ->
			metricsService.generateSubtaskOverview(subtaskTimeline.subtask, subtaskTimeline)
		}
	}
}

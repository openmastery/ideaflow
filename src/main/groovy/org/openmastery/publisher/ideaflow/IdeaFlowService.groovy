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

import org.openmastery.publisher.api.ideaflow.IdeaFlowSubtaskTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.ideaflow.TaskTimelineOverview
import org.openmastery.publisher.api.metrics.DetailedSubtaskReport
import org.openmastery.publisher.api.metrics.TimelineMetrics
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.TaskService
import org.openmastery.publisher.core.activity.BlockActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineGenerator
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineSplitter
import org.openmastery.publisher.metrics.subtask.RiskSummaryCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class IdeaFlowService {

	@Autowired
	private IdeaFlowPersistenceService persistenceService;

	@Autowired
	private TaskService taskService


	TaskTimelineOverview generateTimelineOverviewForTask(Long taskId) {
		Task task = taskService.findTaskWithId(taskId)
		IdeaFlowTimeline timeline = generateIdeaFlowForTask(task);
		List<TimelineMetrics> subtaskTimelineMetrics = generateTimelineMetricsBySubtask(timeline);

		TaskTimelineOverview.builder()
				.task(task)
				.timeline(timeline)
				.subtaskTimelineMetrics(subtaskTimelineMetrics)
				.build()
	}

	/**
	 * Generates the primary IdeaFlowTimeline that can be used for visualization,
	 * or input to metrics calculations.
	 *
	 * @param taskId
	 * @return IdeaFlowTimeline
	 */
	private IdeaFlowTimeline generateIdeaFlowForTask(Task task) {

		List<ModificationActivityEntity> modifications = persistenceService.getModificationActivityList(task.id)
		List<EventEntity> events = persistenceService.getEventList(task.id)
		List<ExecutionActivityEntity> executions = persistenceService.getExecutionActivityList(task.id)
		List<BlockActivityEntity> blocks = persistenceService.getBlockActivityList(task.id)
		List<IdleActivityEntity> idleActivities = persistenceService.getIdleActivityList(task.id)


		IdeaFlowTimeline timeline = new IdeaFlowTimelineGenerator()
				.task(task)
				.modificationActivities(modifications)
				.events(events)
				.executionActivities(executions)
				.blockActivities(blocks)
				.idleActivities(idleActivities)
				.generate()

		return timeline
	}

	private List<TimelineMetrics> generateTimelineMetricsBySubtask(IdeaFlowTimeline timeline) {
		RiskSummaryCalculator riskSummaryCalculator = new RiskSummaryCalculator()
		List<IdeaFlowSubtaskTimeline> subtaskTimelineList = new IdeaFlowTimelineSplitter()
				.timeline(timeline)
				.splitBySubtaskEvents()

		subtaskTimelineList.collect { IdeaFlowSubtaskTimeline subtaskTimeline ->
			riskSummaryCalculator.calculateSubtaskMetrics(subtaskTimeline.subtask, subtaskTimeline)
		}
	}

	DetailedSubtaskReport generateDetailedSubtaskReport(Long taskId, Long subTaskId) {
		return null;
	}
}

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
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline

import org.openmastery.publisher.api.metrics.SubtaskMetrics
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.activity.BlockActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.TaskService

import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineGenerator
import org.openmastery.publisher.metrics.subtask.RiskSummaryCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class IdeaFlowService {

	@Autowired
	private IdeaFlowPersistenceService persistenceService;

	@Autowired
	private TaskService taskService

	/**
	 * Generates the primary IdeaFlowTimeline that can be used for visualization,
	 * or input to metrics calculations.
	 *
	 * @param taskId
	 * @return IdeaFlowTimeline
	 */
	IdeaFlowTimeline generateIdeaFlowForTask(Long taskId) {

		Task task = taskService.findTaskWithId(taskId)
		List<ModificationActivityEntity> modifications = persistenceService.getModificationActivityList(taskId)
		List<EventEntity> events = persistenceService.getEventList(taskId)
		List<ExecutionActivityEntity> executions = persistenceService.getExecutionActivityList(taskId)
		List<BlockActivityEntity> blocks = persistenceService.getBlockActivityList(taskId)
		List<IdleActivityEntity> idleActivities = persistenceService.getIdleActivityList(taskId)


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

	//TODO slice the timeline by subtask, for now, treat the whole timeline as the first subtask
	List<SubtaskMetrics> generateRiskSummariesBySubtask(IdeaFlowTimeline timeline) {


		List<Event> subtaskEvents = timeline.getEvents().findAll { Event event ->
			event.type == EventType.SUBTASK
		}

		RiskSummaryCalculator riskSummaryCalculator =  new RiskSummaryCalculator()

		//TODO make this execute in a loop, slice timelines and collect a list of subtask metrics
		SubtaskMetrics subtaskMetrics = riskSummaryCalculator.calculateSubtaskMetrics(subtaskEvents.first(), timeline)
		return [subtaskMetrics]
	}
}

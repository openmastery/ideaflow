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

import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.IdeaFlowMetrics
import org.openmastery.publisher.api.metrics.CalculatorSpecification
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.activity.BlockActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.TaskService
import org.openmastery.publisher.metrics.MetricsService
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineBuilder
import org.openmastery.publisher.metrics.calculator.RiskSummaryBySubtaskCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class IdeaFlowService {

	@Autowired
	private IdeaFlowPersistenceService persistenceService;

	@Autowired
	private TaskService taskService

	@Autowired
	private MetricsService metricsService

	/**
	 * Generates the primary IdeaFlowTimeline that can be used for visualization,
	 * or input to metrics calculations.
	 *
	 * @see IdeaFlowMetrics
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


		IdeaFlowTimeline timeline = new IdeaFlowTimelineBuilder()
				.task(task)
				.modificationActivities(modifications)
				.events(events)
				.executionActivities(executions)
				.blockActivities(blocks)
				.idleActivities(idleActivities)
				.build()

		return timeline
	}

	IdeaFlowMetrics runYourMetrics(Long taskId, CalculatorSpecification calculatorSpec) {
		IdeaFlowTimeline timeline = generateIdeaFlowForTask(taskId)
		return metricsService.calculateMetrics(timeline, calculatorSpec)
	}


	IdeaFlowMetrics generateRiskSummariesBySubtask(Long taskId) {
		IdeaFlowTimeline timeline = generateIdeaFlowForTask(taskId)
		return metricsService.calculateMetrics(timeline, new RiskSummaryBySubtaskCalculator());
	}
}

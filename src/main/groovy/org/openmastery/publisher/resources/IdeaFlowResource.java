/**
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
package org.openmastery.publisher.resources;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline;
import org.openmastery.publisher.api.ideaflow.TaskTimelineOverview;
import org.openmastery.publisher.api.metrics.DetailedSubtaskReport;
import org.openmastery.publisher.ideaflow.IdeaFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.IDEAFLOW_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class IdeaFlowResource {

	@Autowired
	private IdeaFlowService ideaFlowService;


	/**
	 * Uses the automated data collection from IdeaFlowMetrics to generate an IdeaFlowTimeline.
	 * The timeline includes both the automated data collection, as well as a set of named ToggleableOverlays.
	 *
	 * DetailedStatistics are also available on demand, queryable by subtask.
	 *
	 * @see "http://github.com/openmastery/ideaflowmetrics"
	 * @param taskId
	 * @return IdeaFlowTimeline
	 */

	@GET
	@Path(ResourcePaths.IDEAFLOW_TIMELINE + ResourcePaths.IDEAFLOW_TASK + "/{taskId}")
	public TaskTimelineOverview getTimelineOverviewForTask(@PathParam("taskId") Long taskId) {

		return ideaFlowService.generateTimelineOverviewForTask(taskId);
	}

	@GET
	@Path(ResourcePaths.IDEAFLOW_TIMELINE + ResourcePaths.IDEAFLOW_TASK + "/{taskId}" + ResourcePaths.IDEAFLOW_SUBTASK + "/{subtaskId}")
	public IdeaFlowTaskTimeline getTimelineOverviewForSubtask(@PathParam("taskId") Long taskId, @PathParam("subtaskId") Long subtaskId) {

//		return ideaFlowService.generateIdeaFlowForTask(taskId);
		throw new RuntimeException("implement");
	}

	/**
	 *
	 * Generate summary metrics for all IdeaFlow subtasks
	 * Will eventually be able to provide your own user-configurable metrics.
	 *
	 * Community-contributed, individually-branded OSS IdeaFlow metrics coming soon!
	 *
	 * @param taskId
	 * @return List<TimelineMetrics>
	 */

//	@GET
//	@Path(ResourcePaths.IDEAFLOW_METRICS + ResourcePaths.IDEAFLOW_TASK + "/{taskId}")
//	public List<TimelineMetrics> generateRiskSummariesBySubtask(@PathParam("taskId") Long taskId) {
//
//		return ideaFlowService.generateRiskSummariesBySubtask(taskId);
//	}

	/**
	 *
	 * @param taskId
	 * @param subTaskId
	 * @return
	 */
	@GET
	@Path(ResourcePaths.IDEAFLOW_METRICS + ResourcePaths.IDEAFLOW_TASK + "/{taskId}" + ResourcePaths.IDEAFLOW_SUBTASK + "/{subTaskId}")
	public DetailedSubtaskReport generateDetailedSubtaskReport(@PathParam("taskId") Long taskId, @PathParam("subTaskId") Long subTaskId) {

		return ideaFlowService.generateDetailedSubtaskReport(taskId, subTaskId);
	}



}

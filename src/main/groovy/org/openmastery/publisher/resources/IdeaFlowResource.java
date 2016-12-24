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
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline;
import org.openmastery.publisher.api.metrics.SubtaskMetrics;
import org.openmastery.publisher.ideaflow.IdeaFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
	public IdeaFlowTimeline getTimelineForTask(@PathParam("taskId") Long taskId) {

		return ideaFlowService.generateIdeaFlowForTask(taskId);
	}


	/**
	 *
	 * Generate summary metrics for all IdeaFlow subtasks
	 * Will eventually be able to provide your own user-configurable metrics.
	 *
	 * Community-contributed, individually-branded OSS IdeaFlow metrics coming soon!
	 *
	 * @param taskId
	 * @return List<SubtaskMetrics>
	 */

	@GET
	@Path(ResourcePaths.IDEAFLOW_METRICS + ResourcePaths.IDEAFLOW_TASK + "/{taskId}")
	public List<SubtaskMetrics> generateRiskSummariesBySubtask(@PathParam("taskId") Long taskId) {
		IdeaFlowTimeline timeline = ideaFlowService.generateIdeaFlowForTask(taskId);

		return ideaFlowService.generateRiskSummariesBySubtask(timeline);
	}


}

/**
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
package org.openmastery.publisher.resources;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.ideaflow.SubtaskTimelineOverview;
import org.openmastery.publisher.api.ideaflow.TaskTimelineOverview;
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
	 * Generate an IdeaFlowTimeline and calculate metrics for each subtask to give an overview of a task.
	 * Tasks are broken down into subtasks
	 *
	 * @param taskId
	 * @return IdeaFlowTimeline
	 */

	@GET
	@Path(ResourcePaths.IDEAFLOW_TIMELINE + ResourcePaths.IDEAFLOW_TASK + "/{taskId}")
	public TaskTimelineOverview getTimelineOverviewForTask(@PathParam("taskId") Long taskId) {

		return ideaFlowService.generateTimelineOverviewForTask(taskId);
	}


	/**
	 * Generate drill-downable detailed overview of a subtask, along with a timeline, progress, and metrics overview
	 *
	 * @param taskId
	 * @param subtaskId
	 * @return SubtaskTimelineOverview
	 */
	@GET
	@Path(ResourcePaths.IDEAFLOW_TIMELINE + ResourcePaths.IDEAFLOW_TASK + "/{taskId}" + ResourcePaths.IDEAFLOW_SUBTASK + "/{subtaskId}")
	public SubtaskTimelineOverview getTimelineOverviewForSubtask(@PathParam("taskId") Long taskId, @PathParam("subtaskId") Long subtaskId) {

		return ideaFlowService.generateTimelineOverviewForSubtask(taskId, subtaskId);
	}




}

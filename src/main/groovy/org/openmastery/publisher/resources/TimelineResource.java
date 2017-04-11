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
import org.openmastery.publisher.api.ideaflow.TaskTimelineWithAllSubtasks;
import org.openmastery.publisher.ideaflow.IdeaFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.IDEAFLOW_PATH + ResourcePaths.IDEAFLOW_TIMELINE)
@Produces(MediaType.APPLICATION_JSON)
public class TimelineResource {

	@Autowired
	private IdeaFlowService ideaFlowService;


	/**
	 * Returns an IdeaFlowTimeline with metrics calculated for each subtask to give an overview of a task.
	 * Tasks are broken down into subtasks, but none of the subtask details are available.
	 * This object includes a task-level overview only.
	 *
	 * @param taskId
	 * @return TaskTimelineOverview
	 */

	@GET
	@Path(ResourcePaths.IDEAFLOW_TASK + "/{taskId}")
	public TaskTimelineOverview getTimelineOverviewForTask(@PathParam("taskId") Long taskId) {

		return ideaFlowService.generateTimelineOverviewForTask(taskId);
	}


	/**
	 * Returns an IdeaFlowTimeline with detailed metrics, and IdeaFlowSubtaskTimelines for each subtask.
	 * The IdeaFlowStoryTree is fully-populated with all available tree metrics, with correlated references
	 * between the timelines and the tree.
	 *
	 * This is the all-in-one heavy-weight object API, that returns everything you might possibly want to know
	 * about the task within a single call.
	 *
	 * @param taskId
	 * @return TaskTimelineWithAllSubtasks
	 */

	@GET
	@Path(ResourcePaths.IDEAFLOW_TASK + "/{taskId}" + ResourcePaths.IDEAFLOW_FULL)
	public TaskTimelineWithAllSubtasks getTimelineOverviewForTaskWithAllSubtasks(@PathParam("taskId") Long taskId) {

		return ideaFlowService.generateTimelineWithAllSubtasks(taskId);
	}


	/**
	 * Returns an IdeaFlowSubtaskTimeline for the requested subtask, along with an IdeaFlowStoryTree
	 * with the branch that corresponds to the selected subtask fully populated with all available tree metrics,
	 * with correlated references between the timeline and the tree.
	 *
	 * @param taskId
	 * @param subtaskId
	 * @return SubtaskTimelineOverview
	 */
	@GET
	@Path(ResourcePaths.IDEAFLOW_TASK + "/{taskId}" + ResourcePaths.IDEAFLOW_SUBTASK + "/{subtaskId}")
	public SubtaskTimelineOverview getTimelineOverviewForSubtask(@PathParam("taskId") Long taskId, @PathParam("subtaskId") Long subtaskId) {

		return ideaFlowService.generateTimelineOverviewForSubtask(taskId, subtaskId);
	}






}

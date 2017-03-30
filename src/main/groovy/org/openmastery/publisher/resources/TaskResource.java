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

import org.openmastery.publisher.api.PagedResult;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.task.NewTask;
import org.openmastery.publisher.api.task.Task;
import org.openmastery.publisher.api.task.TaskPatch;
import org.openmastery.publisher.core.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.IDEAFLOW_PATH + ResourcePaths.TASK_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

	@Autowired
	private TaskService taskService;

	@POST
	public Task create(NewTask newTask) {
		return taskService.create(newTask);
	}

	/**
	 * Retrieve the details for a specific task
	 * @param taskId the persistent id
	 * @return Task
	 */
	@GET
	@Path(ResourcePaths.ID_PATH + "/{id}")
	public Task findTaskWithId(@PathParam("id") Long taskId) {
		return taskService.findTaskWithId(taskId);
	}


	/**
	 * Updates details for a task like name, description, and project.
	 * Intended to be used for inline editing of task details
	 *
	 * @param taskPatch TaskPatch object with name, description, project updates
	 * @return Task
	 */

	@PUT //TODO change to PATCH
	@Path(ResourcePaths.ID_PATH + "/{id}")
	public Task updateTask(@PathParam("id") Long taskId, TaskPatch taskPatch) {
		Task updatedTask;
			updatedTask = taskService.updateTask(taskId, taskPatch);

		return updatedTask;
	}

	/**
	 * Find a task using the name rather than the id.
	 * Task names must be unique for a specific user
	 *
	 * @param taskName The name of the task
	 * @return Task
	 */
	@GET
	@Path(ResourcePaths.TASK_NAME_PATH + "/{name}")
	public Task findTaskWithName(@PathParam("name") String taskName) {
		Task task = taskService.findTaskWithName(taskName);
		if (task == null) {
			throw new NotFoundException();
		}
		return task;
	}

	/**
	 * List all the recent tasks sorted by most recently modified tasks (sort order not currently configurable)
	 * Tasks are modified everytime new event or activity data is saved for the task
	 *
	 * @param tags the hashtags to filter for (searches FAQ and event comments)
	 * @param project optional project name filter
	 * @param pageNumber the page number to retrieve, defaults to page 0
	 * @param elementsPerPage the number of tasks per page to retrieve, defaults to 10
	 * @return PagedResult<Task>
	 */

	@GET
	public PagedResult<Task> findRecentTasks(@QueryParam("tag") List<String> tags,
											 @QueryParam("project") String project,
											 @DefaultValue("0") @QueryParam("page_number") Integer pageNumber,
									  		@DefaultValue("10") @QueryParam("per_page") Integer elementsPerPage ) {
		if (tags != null && tags.size() > 0) {
			return taskService.findRecentTasksMatchingTags(project, tags, pageNumber, elementsPerPage);
		} else {
			return taskService.findRecentTasks(project, pageNumber, elementsPerPage);
		}
	}




}

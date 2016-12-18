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

import com.bancvue.rest.exception.ConflictingEntityException;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.task.NewTask;
import org.openmastery.publisher.api.task.Task;
import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.openmastery.publisher.core.activity.IdleActivityEntity;
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateMachine;
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateMachineFactory;
import org.openmastery.publisher.core.task.TaskEntity;
import org.openmastery.mapper.EntityMapper;
import org.openmastery.publisher.core.task.TaskService;
import org.openmastery.publisher.security.InvocationContext;
import org.openmastery.time.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.time.LocalDateTime;

@Component
@Path(ResourcePaths.TASK_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

	@Autowired
	private TaskService taskService;

	@POST
	public Task create(NewTask newTask) {
		return taskService.create(newTask);
	}

	@GET
	@Path(ResourcePaths.ID_PATH + "/{id}")
	public Task findTaskWithId(@PathParam("id") Long taskId) {
		Task task = taskService.findTaskWithId(taskId);
		if (task == null) {
			throw new NotFoundException();
		}
		return task;
	}

	@GET
	@Path(ResourcePaths.TASK_NAME_PATH + "/{name}")
	public Task findTaskWithName(@PathParam("name") String taskName) {
		Task task = taskService.findTaskWithName(taskName);
		if (task == null) {
			throw new NotFoundException();
		}
		return task;
	}

	@GET //TODO implement paging
	public List<Task> findRecentTasks(@QueryParam("page") Integer page,
									  @QueryParam("per_page") Integer perPage) {

		Integer activePage = page == null ? 0 : page;
		Integer activePerPage = perPage == null ? 20 : perPage;

		return taskService.findRecentTasks(activePage, activePerPage);
	}


}

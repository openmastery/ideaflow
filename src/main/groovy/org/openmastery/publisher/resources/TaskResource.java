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
	private IdeaFlowStateMachineFactory stateMachineFactory;
	@Autowired
	private IdeaFlowPersistenceService persistenceService;
	@Autowired
	private TimeService timeService;

	private EntityMapper entityMapper = new EntityMapper();

	private Task toApiTask(TaskEntity taskEntity) {
		return entityMapper.mapIfNotNull(taskEntity, Task.class);
	}

	@POST
	public Task create(NewTask newTask) {
		TaskEntity task = TaskEntity.builder()
				.name(newTask.getName())
				.description(newTask.getDescription())
				.creationDate(timeService.now())
				.build();

		TaskEntity existingTask = persistenceService.findTaskWithName(task.getName());
		if (existingTask != null) {
			throw new ConflictingTaskException(toApiTask(existingTask));
		}

		try {
			task = persistenceService.saveTask(task);
		} catch (DataIntegrityViolationException ex) {
			existingTask = persistenceService.findTaskWithName(task.getName());
			throw new ConflictingTaskException(toApiTask(existingTask));
		}

		IdeaFlowStateMachine stateMachine = stateMachineFactory.createStateMachine(task.getId());
		stateMachine.startTask();

		Task apiTask = entityMapper.mapIfNotNull(task, Task.class);
		return apiTask;
	}

	@PUT
	@Path(ResourcePaths.ACTIVATE_PATH + "/{id}")
	public Task activate(@PathParam("id") Long taskId) {
		TaskEntity task = persistenceService.findTaskWithId(taskId);
		if (task == null) {
			throw new NotFoundException();
		}

		LocalDateTime activityEnd = persistenceService.getMostRecentActivityEnd(taskId);
		if (activityEnd != null) {
			IdleActivityEntity idleTime = IdleActivityEntity.builder()
					.taskId(taskId)
					.start(activityEnd)
					.end(timeService.now())
					.build();
			persistenceService.saveActivity(idleTime);
		}

		return toApiTask(task);
	}

	@GET
	@Path(ResourcePaths.ID_PATH + "/{id}")
	public Task findTaskWithId(@PathParam("id") Long taskId) {
		TaskEntity task = persistenceService.findTaskWithId(taskId);
		if (task == null) {
			throw new NotFoundException();
		}
		return toApiTask(task);
	}

	@GET
	public Task findTaskWithName(@QueryParam("taskName") String taskName) {
		TaskEntity task = persistenceService.findTaskWithName(taskName);
		if (task == null) {
			throw new NotFoundException();
		}
		return toApiTask(task);
	}

	@GET
	@Path(ResourcePaths.RECENT_PATH)
	public List<Task> findRecentTasks(@QueryParam("limit") Integer limit) {
		if (limit == null) {
			limit = 5;
		}
		List<TaskEntity> taskList = persistenceService.findRecentTasks(limit);
		return entityMapper.mapList(taskList, Task.class);
	}


	class ConflictingTaskException extends ConflictingEntityException {
		ConflictingTaskException(Task existingTask) {
			super("Task with name '" + existingTask.getName() + "' already exists", existingTask);
		}
	}

}

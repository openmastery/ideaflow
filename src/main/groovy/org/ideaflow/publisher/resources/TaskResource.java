package org.ideaflow.publisher.resources;

import com.bancvue.rest.exception.ConflictingEntityException;
import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.task.NewTask;
import org.ideaflow.publisher.api.task.Task;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateMachine;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateMachineFactory;
import org.ideaflow.publisher.core.task.TaskEntity;
import org.openmastery.mapper.EntityMapper;
import org.openmastery.time.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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

		return entityMapper.mapIfNotNull(task, Task.class);
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


	class ConflictingTaskException extends ConflictingEntityException {
		ConflictingTaskException(Task existingTask) {
			super("Task with name '" + existingTask.getName() + "' already exists", existingTask);
		}
	}

}

package org.ideaflow.publisher.resources;

import org.ideaflow.publisher.api.NewTask;
import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.Task;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Random;

@Component
@Path(ResourcePaths.TASK_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

	@POST
	public Task create(NewTask task) {
		System.out.println("Create task: " + task);
		return Task.builder()
				.taskId(new Random().nextLong())
				.taskName(task.getTaskName())
				.projectName(task.getProjectName())
				.build();
	}

	@GET
	public Task findTaskWithName(@QueryParam("taskName") String taskName) {
		System.out.println("Find task with name: " + taskName);
		return Task.builder()
				.taskName(taskName)
				.projectName(taskName)
				.taskId(1L)
				.build();
	}

}

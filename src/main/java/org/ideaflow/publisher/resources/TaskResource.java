package org.ideaflow.publisher.resources;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.Task;
import org.springframework.stereotype.Component;

@Component
@Path(ResourcePaths.TASK_PATH + "/{taskId}")
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

	@PUT
	@Path(ResourcePaths.ACTIVATE_PATH)
	public void activate(@PathParam("taskId") String taskId) {
		System.out.println("Activate task: " + taskId);
	}

	@POST
	public void create(@PathParam("taskId") String taskId, Task task) {
		task.setTaskId(taskId);
		System.out.println("Create task: " + task);
	}

}

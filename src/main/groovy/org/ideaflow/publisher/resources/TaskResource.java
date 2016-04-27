package org.ideaflow.publisher.resources;

import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.Task;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.TASK_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

	@PUT
	@Path("/{taskId}" + ResourcePaths.ACTIVATE_PATH)
	public void activate(@PathParam("taskId") String taskId) {
		System.out.println("Activate task: " + taskId);
	}


	@POST
	public void create(Task task) {
		System.out.println("Create task: " + task);
	}

	@GET
	public Task active() {
		System.out.println("Get active");
		return new Task();
	}

}

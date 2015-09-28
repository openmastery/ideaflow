package com.ideaflow.localagent.resources;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;

import static com.ideaflow.localagent.api.ResourcePaths.ACTIVITY_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.CONFLICT_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.START_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.STOP_PATH;

@Component
@Path(ACTIVITY_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ActivityResource {

	@PUT
	@Path("{taskId}" + CONFLICT_PATH + START_PATH)
	public void startConflict(@PathParam("taskId") String taskId) {
		System.out.println("Start Conflict, taskId=" + taskId);
	}

	@PUT
	@Path("{taskId}" + CONFLICT_PATH + STOP_PATH)
	public void stopConflict(@PathParam("taskId") String taskId) {
		System.out.println("Stop Conflict, taskId=" + taskId);
	}

}

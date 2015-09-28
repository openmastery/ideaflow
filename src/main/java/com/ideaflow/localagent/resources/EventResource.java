package com.ideaflow.localagent.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;

import static com.ideaflow.localagent.api.ResourcePaths.EVENT_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.CONFLICT_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.START_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.STOP_PATH;

@Component
@Path(EVENT_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

	@POST
	@Path(CONFLICT_PATH + START_PATH)
	public void startConflict() {
		System.out.println("Start Conflict");
	}

	@POST
	@Path(CONFLICT_PATH + STOP_PATH)
	public void stopConflict() {
		System.out.println("Stop Conflict");
	}

}

package com.ideaflow.localagent.resources;

import com.ideaflow.localagent.api.BandStart;
import com.ideaflow.localagent.api.ConflictEnd;
import com.ideaflow.localagent.api.ConflictStart;
import com.ideaflow.localagent.api.Message;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;

import static com.ideaflow.localagent.api.ResourcePaths.COMMIT_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.CONFLICT_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.EVENT_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.LEARNING_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.NOTE_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.REWORK_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.START_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.STOP_PATH;
import static com.ideaflow.localagent.api.ResourcePaths.TASK_PATH;

@Component
@Path(TASK_PATH + "/{taskName}" + EVENT_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

	@POST
	@Path(CONFLICT_PATH + START_PATH)
	public void startConflict(@PathParam("taskName") String taskName, ConflictStart conflictStart) {
		System.out.println("Start Conflict: " + taskName + ", " + conflictStart);
	}

	@POST
	@Path(CONFLICT_PATH + STOP_PATH)
	public void stopConflict(@PathParam("taskName") String taskName, ConflictEnd conflictEnd) {
		System.out.println("Stop Conflict: " + taskName + ", " + conflictEnd);
	}

    @POST
    @Path(LEARNING_PATH + START_PATH)
    public void startLearning(@PathParam("taskName") String taskName, BandStart bandStart) {
        System.out.println("Start Learning: " + taskName + ", " + bandStart);
    }

    @POST
    @Path(LEARNING_PATH + STOP_PATH)
    public void stopLearning(@PathParam("taskName") String taskName) {
        System.out.println("Stop Learning: " + taskName);
    }

    @POST
    @Path(REWORK_PATH + START_PATH)
    public void startRework(@PathParam("taskName") String taskName, BandStart bandStart) {
        System.out.println("Start Rework: " + taskName + ", " + bandStart);
    }

    @POST
    @Path(REWORK_PATH + STOP_PATH)
    public void stopRework(@PathParam("taskName") String taskName) {
        System.out.println("Stop Rework: " + taskName);
    }

    @POST
    @Path(NOTE_PATH)
    public void addNote(@PathParam("taskName") String taskName, Message message) {
        System.out.println("Add Note: " + taskName + ", " + message);
    }

    @POST
    @Path(COMMIT_PATH)
    public void addCommit(@PathParam("taskName") String taskName, Message message) {
        System.out.println("Add Commit: " + taskName + ", " + message);
    }

}

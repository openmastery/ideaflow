package org.ideaflow.publisher.resources;

import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.timeline.Timeline;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Component
@Path("/stubtimeline")
@Produces(MediaType.APPLICATION_JSON)
public class StubTimelineResource {

	@GET
	@Path(ResourcePaths.TASK_PATH + "/{taskId}")
	public Timeline getTimelineForTask(@PathParam("taskId") String taskId, @QueryParam("userId") String userId) {
		TestDataSupport support = new TestDataSupport();

		switch(taskId) {
			case "trial":
				return support.createTrialAndErrorMap();
			case "learning":
				return support.createLearningNestedConflictMap();
			case "detailed":
				return support.createDetailedConflictMap();
			case "basic":
			default:
				return support.createBasicTimelineWithAllBandTypes();
		}
	}

}

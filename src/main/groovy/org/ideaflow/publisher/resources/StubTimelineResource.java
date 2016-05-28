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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Path("/stubtimeline")
@Produces(MediaType.APPLICATION_JSON)
public class StubTimelineResource {

	@GET
	@Path(ResourcePaths.TIMELINE_NATURAL_PATH + ResourcePaths.TASK_PATH + "/{taskId}")
	public Timeline getTimelineForTask(@PathParam("taskId") String taskId) {
		TestDataSupport support = new TestDataSupport();
		support.disableTimelineSplitter();
		return support.createTimeline(taskId);
	}

	@GET
	@Path(ResourcePaths.TASK_PATH)
	public List<String> getTimelineForTask() {
		TestDataSupport support = new TestDataSupport();
		return support.getTaskIds();
	}

}

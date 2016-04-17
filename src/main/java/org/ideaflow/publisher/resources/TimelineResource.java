package org.ideaflow.publisher.resources;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.TimelineSegment;
import org.springframework.stereotype.Component;

@Component
@Path(ResourcePaths.TIMELINE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class TimelineResource {

	@GET
	@Path(ResourcePaths.TASK_PATH + "/{taskId}")
	public TimelineSegment getTimelineSegmentForTask(@PathParam("taskId") String taskId, @QueryParam("userId") String userId) {
		// TODO: fill me in
		return new TimelineSegment();
	}

	@GET
	@Path(ResourcePaths.DAY_PATH)
	public TimelineSegment getTimelineSegmentForDay(@QueryParam("day") LocalDate day, @QueryParam("userId") String userId) {
		return new TimelineSegment();
	}

	@GET
	@Path(ResourcePaths.DAY_PATH + ResourcePaths.RECENT_PATH)
	public List<TimelineSegment> getRecentTimelineSegmentsForDays(@QueryParam("days") int days, @QueryParam("userId") String userId) {
		return Collections.emptyList();
	}

	@GET
	@Path(ResourcePaths.USER_PATH + ResourcePaths.RECENT_PATH)
	public List<TimelineSegment> getRecentTimelineSegmentsForUser(@QueryParam("userId") String userId) {
		return Collections.emptyList();
	}

	@GET
	@Path(ResourcePaths.PROJECT_PATH + ResourcePaths.RECENT_PATH)
	public List<TimelineSegment> getRecentTimelineSegmentsForProject(@QueryParam("projectId") String projectId) {
		return Collections.emptyList();
	}

}

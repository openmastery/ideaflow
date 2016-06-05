package org.ideaflow.publisher.resources;

import com.bancvue.rest.exception.NotFoundException;
import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.timeline.BandTimeline;
import org.ideaflow.publisher.api.timeline.BandTimelineSegment;
import org.ideaflow.publisher.api.timeline.TreeTimeline;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService;
import org.ideaflow.publisher.core.task.TaskEntity;
import org.ideaflow.publisher.core.timeline.TimelineGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.TIMELINE_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class TimelineResource {

	@Autowired
	private TimelineGenerator timelineGenerator;
	@Autowired
	private IdeaFlowPersistenceService persistenceService;

	@GET
	@Path(ResourcePaths.TIMELINE_BAND_PATH)
	public BandTimeline getBandTimelineForTask(@QueryParam("taskId") Long taskId, @QueryParam("taskName") String taskName) {
		Long timebandTaskId = getTaskId(taskId, taskName);
		return timelineGenerator.createBandTimelineForTask(timebandTaskId);
	}

	@GET
	@Path(ResourcePaths.TIMELINE_TREE_PATH)
	public TreeTimeline getTreeTimelineTreeForTask(@QueryParam("taskId") Long taskId, @QueryParam("taskName") String taskName) {
		Long timebandTaskId = getTaskId(taskId, taskName);
		return timelineGenerator.createTreeTimelineForTask(timebandTaskId);
	}

	private Long getTaskId(Long taskId, String taskName) {
		if (taskId != null) {
			return taskId;
		}
		if (taskName != null) {
			TaskEntity task = persistenceService.findTaskWithName(taskName);
			if (task == null) {
				throw new NotFoundException("No task with name=" + taskName);
			}
			return task.getId();
		}
		throw new NotFoundException("Neither taskId nor taskName found");
	}

//
//	@GET
//	@Path(ResourcePaths.DAY_PATH)
//	public Timeline getTimelineForDay(@QueryParam("day") LocalDate day, @QueryParam("userId") String userId) {
//		return new Timeline();
//	}
//
//	@GET
//	@Path(ResourcePaths.DAY_PATH + ResourcePaths.RECENT_PATH)
//	public List<Timeline> getRecentTimelinesForDays(@QueryParam("days") int days, @QueryParam("userId") String userId) {
//		return Collections.emptyList();
//	}
//
//	@GET
//	@Path(ResourcePaths.USER_PATH + ResourcePaths.RECENT_PATH)
//	public List<Timeline> getRecentTimelinesForUser(@QueryParam("userId") String userId) {
//		return Collections.emptyList();
//	}
//
//	@GET
//	@Path(ResourcePaths.PROJECT_PATH + ResourcePaths.RECENT_PATH)
//	public List<Timeline> getRecentTimelinesForProject(@QueryParam("projectId") String projectId) {
//		return Collections.emptyList();
//	}

}

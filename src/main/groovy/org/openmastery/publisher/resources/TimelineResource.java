package org.openmastery.publisher.resources;

import com.bancvue.rest.exception.NotFoundException;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.timeline.BandTimeline;
import org.openmastery.publisher.api.timeline.TreeTimeline;
import org.openmastery.publisher.api.timeline.ActivityTimeline;
import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.openmastery.publisher.core.task.TaskEntity;
import org.openmastery.publisher.core.timeline.TimelineGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
	public BandTimeline getBandTimelineForTask(@QueryParam("taskId") Long optionalTaskId, @QueryParam("taskName") String optionalTaskName) {
		Long taskId = getTaskId(optionalTaskId, optionalTaskName);
		return timelineGenerator.createBandTimelineForTask(taskId);
	}

	@GET
	@Path(ResourcePaths.TIMELINE_TREE_PATH)
	public TreeTimeline getTreeTimelineTreeForTask(@QueryParam("taskId") Long optionalTaskId, @QueryParam("taskName") String optionalTaskName) {
		Long taskId = getTaskId(optionalTaskId, optionalTaskName);
		return timelineGenerator.createTreeTimelineForTask(taskId);
	}

	@GET
	@Path(ResourcePaths.TIMELINE_ACTIVITY_PATH)
	public ActivityTimeline getActivityTimelineForTask(@QueryParam("taskId") Long optionalTaskId, @QueryParam("taskName") String optionalTaskName) {
		Long taskId = getTaskId(optionalTaskId, optionalTaskName);
		return timelineGenerator.createActivityTimelineForTask(taskId);
	}

	private Long getTaskId(Long optionalTaskId, String optionalTaskName) {
		if (optionalTaskId != null) {
			return optionalTaskId;
		}
		if (optionalTaskName != null) {
			TaskEntity task = persistenceService.findTaskWithName(optionalTaskName);
			if (task == null) {
				throw new NotFoundException("No task with name=" + optionalTaskName);
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

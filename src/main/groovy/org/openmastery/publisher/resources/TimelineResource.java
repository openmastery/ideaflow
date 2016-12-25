/**
 * Copyright 2016 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.resources;

import com.bancvue.rest.exception.NotFoundException;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.timeline.ActivityTimeline;
import org.openmastery.publisher.api.timeline.BandTimeline;
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
			TaskEntity task = persistenceService.findTaskWithName(-1L, optionalTaskName);
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
//	public IdeaFlowTimeline getTimelineForDay(@QueryParam("day") LocalDate day, @QueryParam("userId") String userId) {
//		return new IdeaFlowTimeline();
//	}
//
//	@GET
//	@Path(ResourcePaths.DAY_PATH + ResourcePaths.RECENT_PATH)
//	public List<IdeaFlowTimeline> getRecentTimelinesForDays(@QueryParam("days") int days, @QueryParam("userId") String userId) {
//		return Collections.emptyList();
//	}
//
//	@GET
//	@Path(ResourcePaths.TIMELINE_USER_PATH + ResourcePaths.RECENT_PATH)
//	public List<IdeaFlowTimeline> getRecentTimelinesForUser(@QueryParam("userId") String userId) {
//		return Collections.emptyList();
//	}
//
//	@GET
//	@Path(ResourcePaths.PROJECT_PATH + ResourcePaths.RECENT_PATH)
//	public List<IdeaFlowTimeline> getRecentTimelinesForProject(@QueryParam("projectId") String projectId) {
//		return Collections.emptyList();
//	}

}

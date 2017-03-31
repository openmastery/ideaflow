/**
 * Copyright 2017 New Iron Group, Inc.
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

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.event.AnnotatedEvent;
import org.openmastery.publisher.api.event.EventPatch;
import org.openmastery.publisher.core.EventService;
import org.openmastery.publisher.security.InvocationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.IDEAFLOW_PATH + ResourcePaths.TASK_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class TaskEventResource {

	@Autowired
	private InvocationContext invocationContext;

	@Autowired
	private EventService eventService;


	//TODO should be able to retrieve

	/**
	 * Updates the description of the specified subtask with the supplied patch
	 * @param taskId
	 * @param subtaskId
	 * @param patch
	 * @return Event
	 */

	@PUT //TODO change to PATCH
	@Path(ResourcePaths.ID_PATH + "/{id}" + ResourcePaths.TASK_SUBTASK_PATH + "/{subtaskId}")
	public AnnotatedEvent updateSubtask(@PathParam("id") Long taskId, @PathParam("subtaskId") Long subtaskId, EventPatch patch) {
		AnnotatedEvent event = new AnnotatedEvent();
		if (patch.getDescription() != null) {
			event.fromEvent(eventService.updateEvent(invocationContext.getUserId(), subtaskId, patch.getDescription()));
		}
		if (patch.getFaq() != null) {
			event = eventService.annotateWithFAQ(invocationContext.getUserId(), subtaskId, patch.getFaq());
		}
		event.setFullPath(ResourcePaths.TASK_PATH + ResourcePaths.ID_PATH + "/" + taskId
						+ ResourcePaths.TASK_SUBTASK_PATH + "/" + subtaskId
		);
		return event;
	}


	/**
	 * Updates the description of the specified journey with the supplied patch
	 * @param taskId
	 * @param subtaskId
	 * @param journeyId
	 * @param patch
	 * @return Event
	 */

	@PUT //TODO change to PATCH
	@Path(ResourcePaths.ID_PATH + "/{id}"
			+ ResourcePaths.TASK_SUBTASK_PATH + "/{subtaskId}"
			+ ResourcePaths.TASK_JOURNEY_PATH + "/{journeyId}"
	)
	public AnnotatedEvent updatePain(@PathParam("id") Long taskId, @PathParam("subtaskId") Long subtaskId,
							@PathParam("journeyId") Long journeyId,
							EventPatch patch) {
		AnnotatedEvent event = new AnnotatedEvent();
		if (patch.getDescription() != null) {
			event.fromEvent(eventService.updateEvent(invocationContext.getUserId(), journeyId, patch.getDescription()));
		}
		if (patch.getFaq() != null) {
			event = eventService.annotateWithFAQ(invocationContext.getUserId(), journeyId, patch.getFaq());
		}
		event.setFullPath(ResourcePaths.TASK_PATH + ResourcePaths.ID_PATH + "/" +taskId
						+ ResourcePaths.TASK_SUBTASK_PATH + "/" +subtaskId
						+ ResourcePaths.TASK_JOURNEY_PATH + "/" + journeyId
		);
		return event;
	}

	/**
	 * Updates the description of the specified pain event with the supplied patch
	 * @param taskId
	 * @param subtaskId
     * @param journeyId
	 * @param painId
	 * @param patch
	 * @return Event
	 */

	@PUT //TODO change to PATCH
	@Path(ResourcePaths.ID_PATH + "/{id}"
			+ ResourcePaths.TASK_SUBTASK_PATH + "/{subtaskId}"
			+ ResourcePaths.TASK_JOURNEY_PATH + "/{journeyId}"
			+ ResourcePaths.TASK_PAIN_PATH + "/{painId}"
	)
	public AnnotatedEvent updatePain(@PathParam("id") Long taskId, @PathParam("subtaskId") Long subtaskId,
							   @PathParam("journeyId") Long journeyId,
							   @PathParam("painId") Long painId,
							   EventPatch patch) {
		AnnotatedEvent event = new AnnotatedEvent();
		if (patch.getDescription() != null) {
			event.fromEvent(eventService.updateEvent(invocationContext.getUserId(), painId, patch.getDescription()));
		}
		if (patch.getFaq() != null) {
			event = eventService.annotateWithFAQ(invocationContext.getUserId(), painId, patch.getFaq());
		}
		event.setFullPath(ResourcePaths.TASK_PATH + ResourcePaths.ID_PATH + "/" +taskId
						+ ResourcePaths.TASK_SUBTASK_PATH + "/" +subtaskId
						+ ResourcePaths.TASK_JOURNEY_PATH + "/" + journeyId
						+ ResourcePaths.TASK_PAIN_PATH + "/" + painId
		);

		return event;
	}

	/**
	 * Updates the description of the specified awesome event with the supplied patch
	 * @param taskId
	 * @param subtaskId
	 * @param journeyId
	 * @param awesomeId
	 * @param patch
	 * @return Event
	 */

	@PUT //TODO change to PATCH
	@Path(ResourcePaths.ID_PATH + "/{id}"
			+ ResourcePaths.TASK_SUBTASK_PATH + "/{subtaskId}"
			+ ResourcePaths.TASK_JOURNEY_PATH + "/{journeyId}"
			+ ResourcePaths.TASK_AWESOME_PATH + "/{awesomeId}"
	)
	public AnnotatedEvent updateAwesome(@PathParam("id") Long taskId, @PathParam("subtaskId") Long subtaskId,
							@PathParam("journeyId") Long journeyId,
							@PathParam("awesomeId") Long awesomeId,
							EventPatch patch) {
		AnnotatedEvent event = new AnnotatedEvent();
		if (patch.getDescription() != null) {
			event.fromEvent(eventService.updateEvent(invocationContext.getUserId(), awesomeId, patch.getDescription()));
		}
		if (patch.getFaq() != null) {
			event = eventService.annotateWithFAQ(invocationContext.getUserId(), awesomeId, patch.getFaq());
		}
		event.setFullPath(ResourcePaths.TASK_PATH + ResourcePaths.ID_PATH + "/" +taskId
				+ ResourcePaths.TASK_SUBTASK_PATH + "/" +subtaskId
				+ ResourcePaths.TASK_JOURNEY_PATH + "/" + journeyId
				+ ResourcePaths.TASK_AWESOME_PATH + "/" + awesomeId
		);
		return event;
	}

	/**
	 * Updates the description of the specified awesome event with the supplied patch
	 * @param taskId
	 * @param subtaskId
	 * @param milestoneId
	 * @param patch
	 * @return Event
	 */

	@PUT //TODO change to PATCH
	@Path(ResourcePaths.ID_PATH + "/{id}"
			+ ResourcePaths.TASK_SUBTASK_PATH + "/{subtaskId}"
			+ ResourcePaths.TASK_MILESTONE_PATH + "/{milestoneId}"
	)
	public AnnotatedEvent updateMilestone(@PathParam("id") Long taskId, @PathParam("subtaskId") Long subtaskId,
							   @PathParam("milestoneId") Long milestoneId,
							   EventPatch patch) {
		AnnotatedEvent event = new AnnotatedEvent();
		if (patch.getDescription() != null) {
			event.fromEvent(eventService.updateEvent(invocationContext.getUserId(), milestoneId, patch.getDescription()));
		}
		if (patch.getFaq() != null) {
			event = eventService.annotateWithFAQ(invocationContext.getUserId(), milestoneId, patch.getFaq());
		}
		event.setFullPath(ResourcePaths.TASK_PATH + ResourcePaths.ID_PATH + "/" +taskId
				+ ResourcePaths.TASK_SUBTASK_PATH + "/" +subtaskId
				+ ResourcePaths.TASK_MILESTONE_PATH + "/" + milestoneId);
		return event;
	}

}

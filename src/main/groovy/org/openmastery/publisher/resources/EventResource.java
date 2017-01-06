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

import org.hibernate.cfg.NotYetImplementedException;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.annotation.FAQAnnotation;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.EventType;
import org.openmastery.publisher.core.EventService;
import org.openmastery.publisher.security.InvocationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.IDEAFLOW_PATH + ResourcePaths.EVENT_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

	@Autowired
	private InvocationContext invocationContext;

	@Autowired
	private EventService eventService;


	/**
	 * Retrieve all the recent events sent to the server after a specific datetime.  Intended to be used for polling.
	 * @param afterDate formatted as yyyyMMdd_HHmmss
	 * @param limit the maximum number of events to return
	 * @return List<Event>
	 */

	@GET
	public List<Event> getLatestEvents(@QueryParam("afterDate") String afterDate, @QueryParam("limit") Integer limit) {
		Long userId = invocationContext.getUserId();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss");
		LocalDateTime jodaAfterDate = formatter.parseLocalDateTime(afterDate);
		return eventService.getLatestEvents(userId, jodaAfterDate, limit);
	}

	/**
	 * Retrieve all the recent events for a specified event type
	 * @param eventType Any of the event subtypes in this resource (journey and experiment not yet supported)
	 * @param afterDate Get events after the specified date
	 * @param limit the maximum number of events to retrieve
	 * @return List<Event>
	 */
	@GET
	@Path("{eventType}")
	public List<Event> getLatestEventsByType(@PathParam("eventType") String eventType, @QueryParam("afterDate") String afterDate, @QueryParam("limit") Integer limit) {
		Long userId = invocationContext.getUserId();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd_HHmmss");
		LocalDateTime jodaAfterDate = formatter.parseLocalDateTime(afterDate);

		return eventService.getLatestEventsByType(userId, EventType.WTF, jodaAfterDate, limit);
	}

	/**
	 * Update the comment for the event
	 * @param eventId the eventId from the relative path
	 * @param comment the comment to save
	 * @return Event
	 */
	@PUT
	@Path(ResourcePaths.TASK_PATH + ResourcePaths.EVENT_TASK_ACTIVATE + "/{eventId}")
	public Event updateActivate(@PathParam("eventId") Long eventId, String comment) {
		Long userId = invocationContext.getUserId();

		return eventService.updateEvent(userId, eventId, comment);
	}

	/**
	 * Update the comment for the event
	 * @param eventId the eventId from the relative path
	 * @param comment the comment to save
	 * @return Event
	 */
	@PUT
	@Path(ResourcePaths.TASK_PATH + ResourcePaths.EVENT_TASK_DEACTIVATE + "/{eventId}")
	public Event updateDeactivate(@PathParam("eventId") Long eventId, String comment) {
		Long userId = invocationContext.getUserId();

		return eventService.updateEvent(userId, eventId, comment);
	}

	/**
	 * Update the comment for the event
	 * @param eventId the eventId from the relative path
	 * @param comment the comment to save
	 * @return Event
	 */
	@PUT
	@Path(ResourcePaths.EVENT_SUBTASK+ "/{eventId}")
	public Event updateSubtask(@PathParam("eventId") Long eventId, String comment) {
		Long userId = invocationContext.getUserId();

		return eventService.updateEvent(userId, eventId, comment);
	}

	/**
	 * Update the comment for the event
	 * @param eventId the eventId from the relative path
	 * @param comment the comment to save
	 * @return Event
	 */

	@PUT
	@Path(ResourcePaths.EVENT_MILESTONE+ "/{eventId}")
	public Event updateProgressMilestone(@PathParam("eventId") Long eventId, String comment) {
		Long userId = invocationContext.getUserId();

		return eventService.updateEvent(userId, eventId, comment);
	}

	/**
	 * Update the comment for the event
	 * @param eventId the eventId from the relative path
	 * @param comment the comment to save
	 * @return Event
	 */

	@PUT
	@Path(ResourcePaths.EVENT_JOURNEY+ "/{eventId}")
	public Event updateJourney(@PathParam("eventId") Long eventId, String comment) {

		//TODO create an annotation for the first event, which is modeled as a journey comment.
		//not currently editable on the UI, so skip this for the moment...

		throw new NotYetImplementedException("Journeys are not yet editable");
	}

	/**
	 * Update the comment for the event
	 * @param eventId the eventId from the relative path
	 * @param comment the comment to save
	 * @return Event
	 */

	@PUT
	@Path(ResourcePaths.EVENT_WTF+ "/{eventId}")
	public Event updateWTF(@PathParam("eventId") Long eventId, String comment) {
		Long userId = invocationContext.getUserId();

		return eventService.updateEvent(userId, eventId, comment);
	}

	/**
	 * Update the comment for the event
	 * @param eventId the eventId from the relative path
	 * @param comment the comment to save
	 * @return Event
	 */

	@PUT
	@Path(ResourcePaths.EVENT_DISCOVERY+ "/{eventId}")
	public Event updateDiscoveryCycle(@PathParam("eventId") Long eventId, String comment) {
		Long userId = invocationContext.getUserId();

		return eventService.updateEvent(userId, eventId, comment);
	}

	/**
	 * Update the comment for the event
	 * @param eventId the eventId from the relative path
	 * @param comment the comment to save
	 * @return Event
	 */

	@PUT
	@Path(ResourcePaths.EVENT_EXPERIMENT+ "/{eventId}")
	public Event updateExperiment(@PathParam("eventId") Long eventId, String comment) {

		//TODO this is an execution event
		//NO-OP this for now, comments on process execution?  I don't see why not...

		throw new NotYetImplementedException("Experiments are not yet editable");
	}


	/**
	 * Annotate an existing event with an FAQ comment.  Old FAQs will be overwritten by new FAQs
	 *
	 * @param eventId the event to annotate
	 * @param faqComment an explanation of what happened augmented with #hashtags
	 * @return FAQAnnotation
	 */

	@POST
	@Path("/{eventId}"+ ResourcePaths.EVENT_ANNOTATION_PATH + ResourcePaths.EVENT_FAQ_PATH)
	public FAQAnnotation saveAnnotation(@PathParam("eventId") Long eventId, String faqComment) {
		Long userId = invocationContext.getUserId();

		return eventService.annotateWithFAQ(userId, eventId, faqComment);
	}

}

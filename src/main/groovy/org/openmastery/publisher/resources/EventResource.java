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

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmastery.mapper.EntityMapper;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.annotation.FAQAnnotation;
import org.openmastery.publisher.api.batch.NewBatchEvent;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.core.EventService;
import org.openmastery.publisher.core.event.EventEntity;
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
	 * Update the comment or position of a NOTE, SUBTASK, DISRUPTION, PAIN, AWESOME or any other event type
	 * @param eventId the event to update
	 * @param eventToUpdate the full event object
	 * @return Event
	 */

	@PUT
	@Path("/{eventId}")
	public Event update(@PathParam("eventId") Long eventId, Event eventToUpdate) {
		Long userId = invocationContext.getUserId();

		return eventService.updateEvent(userId, eventToUpdate);
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

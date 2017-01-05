package org.openmastery.publisher.client;

import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.annotation.FAQAnnotation;
import org.openmastery.publisher.api.batch.NewBatchEvent;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.NewEvent;
import org.openmastery.publisher.api.ideaflow.TaskTimelineOverview;

import java.util.List;

public class EventClient extends OpenMasteryClient<Event, EventClient> {

	public EventClient(String baseUrl) {
		super(baseUrl, ResourcePaths.IDEAFLOW_PATH + ResourcePaths.EVENT_PATH, Event.class);
	}

	public List<Event> getRecentEvents(LocalDateTime afterDate, Integer limit) {
		return crudClientRequest
				.queryParam("afterDate", afterDate.toString("yyyyMMdd_HHmmss"))
				.queryParam("limit", limit)
				.findMany();
	}

	public Event updateEvent(String eventType, Long eventId, String comment) {
		return crudClientRequest
				.path(eventType)
				.path(eventId)
				.updateWithPut(comment);
	}

	public FAQAnnotation annotateWithFAQ(Long eventId, String annotation) {
		return (FAQAnnotation) getUntypedCrudClientRequest()
				.path(eventId)
				.path(ResourcePaths.EVENT_ANNOTATION_PATH)
				.path(ResourcePaths.EVENT_FAQ_PATH)
				.entity(FAQAnnotation.class)
				.createWithPost(annotation);
	}

}

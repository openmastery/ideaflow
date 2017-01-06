package org.openmastery.storyweb.client;

import org.joda.time.LocalDateTime;

import org.openmastery.publisher.api.annotation.FAQAnnotation;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.client.IdeaFlowClient;
import org.openmastery.storyweb.api.ResourcePaths;

import java.util.List;

public class EventClient extends IdeaFlowClient<Event, EventClient> {

	public EventClient(String baseUrl) {
		super(baseUrl, ResourcePaths.STORY_WEB_PATH + ResourcePaths.EVENT_PATH, Event.class);
	}

	//TODO implement paging
	public List<Event> getRecentEvents(LocalDateTime afterDate, Integer limit) {
		return crudClientRequest
				.queryParam("afterDate", afterDate.toString("yyyyMMdd_HHmmss"))
				.queryParam("limit", limit)
				.findMany();
	}

	//TODO implement paging
	public List<Event> getRecentEventsByType(String eventType, LocalDateTime afterDate, Integer limit) {
		return crudClientRequest
				.path(eventType)
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

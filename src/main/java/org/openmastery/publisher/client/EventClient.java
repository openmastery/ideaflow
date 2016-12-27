package org.openmastery.publisher.client;

import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.batch.NewBatchEvent;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.NewEvent;

import java.util.List;

public class EventClient extends OpenMasteryClient<Event, EventClient> {

	public EventClient(String baseUrl) {
		super(baseUrl, ResourcePaths.IDEAFLOW_PATH + ResourcePaths.EVENT_PATH, Event.class);
	}

	public List<Event> getRecentEvents(LocalDateTime afterDate, Integer limit) {
		return (List<Event>) getUntypedCrudClientRequest()
				.queryParam("afterDate", afterDate.toString("yyyyMMdd_HHmmss"))
				.queryParam("limit", limit)
				.findMany();
	}

	public Event updateEvent(Event event) {
		return (Event) getUntypedCrudClientRequest()
				.updateWithPut(event);
	}

}

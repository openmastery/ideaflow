package org.openmastery.publisher.client;

import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.batch.NewBatchEvent;
import org.openmastery.publisher.api.event.NewEvent;

import java.util.List;

public class EventClient extends OpenMasteryClient<NewEvent, EventClient> {

	public EventClient(String baseUrl) {
		super(baseUrl, ResourcePaths.EVENT_PATH, NewEvent.class);
	}

	public List<NewBatchEvent> getRecentEvents(LocalDateTime afterDate, Integer limit) {
		return (List<NewBatchEvent>) getUntypedCrudClientRequest()
				.path(ResourcePaths.EVENT_BATCH_PATH)
				.queryParam("afterDate", afterDate.toString("yyyyMMdd_HHmmss"))
				.queryParam("limit", limit)
				.findMany();
	}

}

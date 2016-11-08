package org.openmastery.publisher.client;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.batch.NewBatchEvent;
import org.openmastery.publisher.api.event.EventType;
import org.openmastery.publisher.api.event.NewEvent;

import java.util.List;

public class EventClient extends OpenMasteryClient<NewEvent, EventClient> {

	public EventClient(String baseUrl) {
		super(baseUrl, ResourcePaths.EVENT_PATH, NewEvent.class);
	}

	private NewEvent createNewEvent(Long taskId, String message) {
		return NewEvent.builder()
					.taskId(taskId)
					.comment(message)
					.build();
	}

	public void createEvent(Long taskId, EventType eventType, String message) {
		if (eventType == EventType.AWESOME) {
			createAwesome(taskId, message);
		} else if (eventType == EventType.SUBTASK) {
			createSubtask(taskId, message);
		} else if (eventType == EventType.NOTE) {
			createNote(taskId, message);
		} else if (eventType == EventType.WTF) {
			createWTF(taskId, message);
		}
	}

	public void createNote(Long taskId, String message) {
		NewEvent event = createNewEvent(taskId, message);
		crudClientRequest.path(ResourcePaths.EVENT_NOTE_PATH)
				.createWithPost(event);
	}

	public void createSubtask(Long taskId, String message) {
		NewEvent event = createNewEvent(taskId, message);
		crudClientRequest.path(ResourcePaths.EVENT_SUBTASK_PATH)
				.createWithPost(event);
	}

	public void createWTF(Long taskId, String message) {
		NewEvent event = createNewEvent(taskId, message);
		crudClientRequest.path(ResourcePaths.EVENT_WTF_PATH)
				.createWithPost(event);
	}

	public void createAwesome(Long taskId, String message) {
		NewEvent event = createNewEvent(taskId, message);
		crudClientRequest.path(ResourcePaths.EVENT_AWESOME_PATH)
				.createWithPost(event);
	}

	public List<NewBatchEvent> getRecentEvents(LocalDateTime afterDate, Integer limit) {

		return (List<NewBatchEvent>) getUntypedCrudClientRequest()
				.path(ResourcePaths.EVENT_BATCH_PATH)
				.queryParam("afterDate", afterDate.toString("yyyyMMdd_HHmmss"))
				.queryParam("limit", limit)
				.findMany();
	}

}

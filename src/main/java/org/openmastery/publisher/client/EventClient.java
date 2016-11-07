package org.openmastery.publisher.client;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.event.EventType;
import org.openmastery.publisher.api.event.NewEvent;

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
			addUserNote(taskId, message);
		} else if (eventType == EventType.WTF) {
			createWTF(taskId, message);
		}
	}

	public void addUserNote(Long taskId, String message) {
		NewEvent event = createNewEvent(taskId, message);
		crudClientRequest.path(ResourcePaths.NOTE_PATH)
				.createWithPost(event);
	}

	public void createSubtask(Long taskId, String message) {
		NewEvent event = createNewEvent(taskId, message);
		crudClientRequest.path(ResourcePaths.SUBTASK_PATH)
				.createWithPost(event);
	}

	public void createWTF(Long taskId, String message) {
		NewEvent event = createNewEvent(taskId, message);
		crudClientRequest.path(ResourcePaths.WTF_PATH)
				.createWithPost(event);
	}

	public void createAwesome(Long taskId, String message) {
		NewEvent event = createNewEvent(taskId, message);
		crudClientRequest.path(ResourcePaths.AWESOME_PATH)
				.createWithPost(event);
	}

}

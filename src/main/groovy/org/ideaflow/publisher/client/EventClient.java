package org.ideaflow.publisher.client;

import org.ideaflow.common.rest.client.CrudClient;
import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.event.NewEvent;

public class EventClient extends CrudClient<NewEvent, EventClient> {

	public EventClient(String baseUrl) {
		super(baseUrl, ResourcePaths.EVENT_PATH, NewEvent.class);
	}

	private NewEvent createNewEvent(Long taskId, String message) {
		return NewEvent.builder()
					.taskId(taskId)
					.comment(message)
					.build();
	}

	public void addUserNote(Long taskId, String message) {
		NewEvent event = createNewEvent(taskId, message);
		crudClientRequest.path(ResourcePaths.NOTE_PATH)
				.createWithPost(event);
	}

	public void startSubtask(Long taskId, String message) {
		NewEvent event = createNewEvent(taskId, message);
		crudClientRequest.path(ResourcePaths.SUBTASK_PATH)
				.createWithPost(event);
	}

}

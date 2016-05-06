package org.ideaflow.publisher.client;

import org.ideaflow.common.rest.client.CrudClient;
import org.ideaflow.publisher.api.ResourcePaths;

public class EventClient extends CrudClient<String, EventClient> {

	public EventClient(String baseUrl) {
		super(baseUrl, ResourcePaths.TASK_PATH, String.class);
	}

	public void addUserNote(String taskId, String message) {
		crudClientRequest.path(taskId)
				.path(ResourcePaths.EDITOR_PATH)
				.path(ResourcePaths.NOTE_PATH)
				.createWithPost(message);
	}

	public void addSubtask(String taskId, String message) {
		crudClientRequest.path(taskId)
				.path(ResourcePaths.EDITOR_PATH)
				.path(ResourcePaths.SUBTASK_PATH)
				.createWithPost(message);
	}

}

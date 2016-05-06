package org.ideaflow.publisher.client;

import org.ideaflow.common.rest.client.CrudClient;
import org.ideaflow.publisher.api.EditorActivity;
import org.ideaflow.publisher.api.ResourcePaths;

public class EditorActivityClient extends CrudClient<EditorActivity, EditorActivityClient> {

	public EditorActivityClient(String baseUrl) {
		super(baseUrl, ResourcePaths.TASK_PATH, EditorActivity.class);
	}

	public void addEditorActivity(String taskId, EditorActivity editorActivity) {
		crudClientRequest.path(taskId)
				.path(ResourcePaths.ACTIVITY_PATH)
				.path(ResourcePaths.EDITOR_PATH)
				.createWithPost(editorActivity);
	}

	public void addIdleActivity(String taskId, EditorActivity editorActivity) {
		crudClientRequest.path(taskId)
				.path(ResourcePaths.ACTIVITY_PATH)
				.path(ResourcePaths.IDLE_PATH)
				.createWithPost(editorActivity);
	}

}

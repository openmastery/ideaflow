package org.ideaflow.publisher.client;

import org.ideaflow.common.rest.client.CrudClient;
import org.ideaflow.publisher.api.EditorActivity;
import org.ideaflow.publisher.api.ResourcePaths;

import java.time.Duration;

public class EditorActivityClient extends CrudClient<EditorActivity, EditorActivityClient> {

	public EditorActivityClient(String baseUrl) {
		super(baseUrl, ResourcePaths.ACTIVITY_PATH, EditorActivity.class);
	}

	private EditorActivity createEditorActivity(Long taskId, String filePath, boolean isModified, Duration duration) {
		return EditorActivity.builder()
					.taskId(taskId)
					.filePath(filePath)
					.isModified(isModified)
					.duration(duration)
					.build();
	}

	public void addEditorActivity(Long taskId, String filePath, boolean isModified, Duration duration) {
		EditorActivity activity = createEditorActivity(taskId, filePath, isModified, duration);
		crudClientRequest.path(ResourcePaths.EDITOR_PATH)
				.createWithPost(activity);
	}

	public void addIdleActivity(Long taskId, String filePath, boolean isModified, Duration duration) {
		EditorActivity activity = createEditorActivity(taskId, filePath, isModified, duration);
		crudClientRequest.path(ResourcePaths.IDLE_PATH)
				.createWithPost(activity);
	}

}

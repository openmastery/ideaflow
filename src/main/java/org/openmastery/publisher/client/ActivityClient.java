package org.openmastery.publisher.client;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.activity.EditorActivity;
import org.openmastery.rest.client.CrudClient;

import java.time.Duration;

public class ActivityClient extends CrudClient<EditorActivity, ActivityClient> {

	public ActivityClient(String baseUrl) {
		super(baseUrl, ResourcePaths.ACTIVITY_PATH, EditorActivity.class);
	}

	public void addEditorActivity(Long taskId, String filePath, boolean isModified, Duration duration) {
		EditorActivity activity = EditorActivity.builder()
					.taskId(taskId)
					.filePath(filePath)
					.isModified(isModified)
					.duration(duration)
					.build();

		crudClientRequest.path(ResourcePaths.EDITOR_PATH)
				.createWithPost(activity);
	}

}

package org.openmastery.publisher.client;

import com.bancvue.rest.client.crud.CrudClient;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.activity.EditorActivity;
import org.openmastery.publisher.api.activity.NewActivityBatch;
import org.openmastery.publisher.api.activity.NewEditorActivity;
import org.openmastery.publisher.api.activity.NewExternalActivity;
import org.openmastery.publisher.api.activity.NewIdleActivity;

public class ActivityClient extends CrudClient<EditorActivity, ActivityClient> {

	public ActivityClient(String baseUrl) {
		super(baseUrl, ResourcePaths.ACTIVITY_PATH, EditorActivity.class);
	}

	public void addActivityBatch(NewActivityBatch batch) {
		crudClientRequest.createWithPost(batch);
	}

	public void addEditorActivity(Long taskId, Long durationInSeconds, String filePath, boolean isModified) {
		NewEditorActivity activity = NewEditorActivity.builder()
					.taskId(taskId)
					.filePath(filePath)
					.isModified(isModified)
					.durationInSeconds(durationInSeconds)
					.build();

		crudClientRequest.path(ResourcePaths.EDITOR_PATH)
				.createWithPost(activity);
	}

	public void addIdleActivity(Long taskId, Long durationInSeconds) {
		NewIdleActivity activity = NewIdleActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.build();

		crudClientRequest.path(ResourcePaths.IDLE_PATH)
				.createWithPost(activity);
	}

	public void addExternalActivity(Long taskId, Long durationInSeconds, String comment) {
		NewExternalActivity activity = NewExternalActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.comment(comment)
				.build();

		crudClientRequest.path(ResourcePaths.EXTERNAL_PATH)
				.createWithPost(activity);
	}

}

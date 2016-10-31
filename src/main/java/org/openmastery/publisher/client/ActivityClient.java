package org.openmastery.publisher.client;

import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.activity.EditorActivity;
import org.openmastery.publisher.api.activity.NewActivityBatch;
import org.openmastery.publisher.api.activity.NewEditorActivity;
import org.openmastery.publisher.api.activity.NewExternalActivity;
import org.openmastery.publisher.api.activity.NewIdleActivity;

import java.util.Arrays;

public class ActivityClient extends OpenMasteryClient<EditorActivity, ActivityClient> {

	public ActivityClient(String baseUrl) {
		super(baseUrl, ResourcePaths.ACTIVITY_PATH, EditorActivity.class);
	}

	public void addActivityBatch(NewActivityBatch batch) {
		crudClientRequest.createWithPost(batch);
	}

	public void addEditorActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds, String filePath, boolean isModified) {
		NewEditorActivity activity = NewEditorActivity.builder()
					.taskId(taskId)
					.filePath(filePath)
					.isModified(isModified)
					.durationInSeconds(durationInSeconds)
					.endTime(endTime)
					.build();

		NewActivityBatch batch = new NewActivityBatch(endTime, Arrays.asList(activity), null, null, null, null);
		addActivityBatch(batch);
	}

	public void addIdleActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds) {
		NewIdleActivity activity = NewIdleActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.endTime(endTime)
				.build();

		NewActivityBatch batch = new NewActivityBatch(endTime, null, null , Arrays.asList(activity), null, null);
		addActivityBatch(batch);
	}

	public void addExternalActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds, String comment) {
		NewExternalActivity activity = NewExternalActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.comment(comment)
				.endTime(endTime)
				.build();

		NewActivityBatch batch = new NewActivityBatch(endTime, null, Arrays.asList(activity), null, null, null);
		addActivityBatch(batch);
	}

}

package org.openmastery.publisher.client;

import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.activity.*;
import org.openmastery.publisher.core.activity.ExecutionActivityEntity;

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

	public void addExecutionActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds, String processName, String executionTaskType,
									 int exitCode, boolean isDebug) {
		NewExecutionActivity newExecutionActivity = NewExecutionActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.processName(processName)
				.executionTaskType(executionTaskType)
				.exitCode(exitCode)
				.isDebug(isDebug)
				.endTime(endTime)
				.build();

		NewActivityBatch batch = NewActivityBatch.builder()
				.timeSent(endTime)
				.executionActivityList(Arrays.asList(newExecutionActivity))
				.build();
		addActivityBatch(batch);
	}

	public void addModificationActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds, Integer fileModificationCount) {
		NewModificationActivity newModificationActivity = NewModificationActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.fileModificationCount(fileModificationCount)
				.endTime(endTime)
				.build();

		NewActivityBatch batch = NewActivityBatch.builder()
				.timeSent(endTime)
				.modificationActivityList(Arrays.asList(newModificationActivity))
				.build();
		addActivityBatch(batch);
	}
}

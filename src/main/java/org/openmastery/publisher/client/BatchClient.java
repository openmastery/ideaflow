package org.openmastery.publisher.client;

import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.activity.*;
import org.openmastery.publisher.api.batch.NewBatchEvent;
import org.openmastery.publisher.api.batch.NewIFMBatch;
import org.openmastery.publisher.api.event.EventType;

import java.util.Arrays;

public class BatchClient extends OpenMasteryClient<EditorActivity, BatchClient> {

	public BatchClient(String baseUrl) {
		super(baseUrl, ResourcePaths.BATCH_PATH, EditorActivity.class);
	}

	public void addIFMBatch(NewIFMBatch batch) {
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

		NewIFMBatch batch = NewIFMBatch.builder().timeSent(endTime).editorActivityList(Arrays.asList(activity)).build();
		addIFMBatch(batch);
	}

	public void addIdleActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds) {
		NewIdleActivity activity = NewIdleActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.endTime(endTime)
				.build();

		NewIFMBatch batch = NewIFMBatch.builder().timeSent(endTime).idleActivityList(Arrays.asList(activity)).build();
		addIFMBatch(batch);
	}

	public void addExternalActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds, String comment) {
		NewExternalActivity activity = NewExternalActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.comment(comment)
				.endTime(endTime)
				.build();

		NewIFMBatch batch = NewIFMBatch.builder().timeSent(endTime).externalActivityList(Arrays.asList(activity)).build();
		addIFMBatch(batch);
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

		NewIFMBatch batch = NewIFMBatch.builder()
				.timeSent(endTime)
				.executionActivityList(Arrays.asList(newExecutionActivity))
				.build();
		addIFMBatch(batch);
	}

	public void addModificationActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds, Integer modificationCount) {
		NewModificationActivity newModificationActivity = NewModificationActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.modificationCount(modificationCount)
				.endTime(endTime)
				.build();

		NewIFMBatch batch = NewIFMBatch.builder()
				.timeSent(endTime)
				.modificationActivityList(Arrays.asList(newModificationActivity))
				.build();
		addIFMBatch(batch);
	}

	public void addBatchEvent(Long taskId, LocalDateTime position, EventType eventType, String comment) {
		NewBatchEvent event = NewBatchEvent.builder()
				.taskId(taskId)
				.position(position)
				.type(eventType)
				.comment(comment)
				.build();

		NewIFMBatch batch = NewIFMBatch.builder()
				.timeSent(position)
				.eventList(Arrays.asList(event))
				.build();
		addIFMBatch(batch);
	}
}

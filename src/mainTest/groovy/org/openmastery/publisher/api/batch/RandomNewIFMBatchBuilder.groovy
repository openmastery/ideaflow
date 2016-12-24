package org.openmastery.publisher.api.batch

import org.joda.time.LocalDateTime
import org.openmastery.publisher.ARandom
import org.openmastery.publisher.api.activity.NewBlockActivity
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewIdleActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.publisher.api.activity.RandomNewEditorActivityBuilder
import org.openmastery.publisher.api.event.EventType
import org.openmastery.time.TimeConverter

class RandomNewIFMBatchBuilder extends NewIFMBatch.NewIFMBatchBuilder {

	RandomNewIFMBatchBuilder() {
		super.blockActivityList([])
			.editorActivityList([])
			.executionActivityList([])
			.externalActivityList([])
			.modificationActivityList([])
			.idleActivityList([])
			.eventList([])
			.timeSent(TimeConverter.toJodaLocalDateTime(ARandom.aRandom.dayOfYear()))
	}

	RandomNewIFMBatchBuilder timeSent(LocalDateTime localDateTime) {
		super.timeSent(localDateTime)
		this
	}

	public RandomNewIFMBatchBuilder newEditorActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds, String filePath, boolean isModified) {
		NewEditorActivity activity = NewEditorActivity.builder()
				.taskId(taskId)
				.filePath(filePath)
				.isModified(isModified)
				.durationInSeconds(durationInSeconds)
				.endTime(endTime)
				.build();
		super.editorActivity(activity)
		return this
	}


	public RandomNewIFMBatchBuilder newIdleActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds) {
		NewIdleActivity activity = NewIdleActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.endTime(endTime)
				.build();

		super.idleActivity(activity)
		return this
	}

	public RandomNewIFMBatchBuilder newExternalActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds, String comment) {
		NewExternalActivity activity = NewExternalActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.comment(comment)
				.endTime(endTime)
				.build();

		super.externalActivity(activity)
		return this
	}

	public RandomNewIFMBatchBuilder newBlockActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds, String comment) {
		NewBlockActivity activity = NewBlockActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.comment(comment)
				.endTime(endTime)
				.build();

		super.blockActivity(activity)
		return this
	}


	public RandomNewIFMBatchBuilder newExecutionActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds, String processName, String executionTaskType,
									 int exitCode, boolean isDebug) {
		NewExecutionActivity activity = NewExecutionActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.processName(processName)
				.executionTaskType(executionTaskType)
				.exitCode(exitCode)
				.isDebug(isDebug)
				.endTime(endTime)
				.build();

		super.executionActivity(activity)
		return this
	}

	public RandomNewIFMBatchBuilder newModificationActivity(Long taskId, LocalDateTime endTime, Long durationInSeconds, Integer modificationCount) {
		NewModificationActivity activity = NewModificationActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.modificationCount(modificationCount)
				.endTime(endTime)
				.build();

		super.modificationActivity(activity)
		return this
	}



	public RandomNewIFMBatchBuilder newEvent(Long taskId, LocalDateTime position, EventType eventType, String comment) {
		NewBatchEvent event = NewBatchEvent.builder()
				.taskId(taskId)
				.position(position)
				.type(eventType)
				.comment(comment)
				.build();

		super.event(event)
		return this
	}


}

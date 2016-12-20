package org.openmastery.publisher.api.activity

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.task.NewTask
import org.openmastery.time.TimeConverter

import static org.openmastery.publisher.ARandom.aRandom

class RandomNewEditorActivityBuilder extends NewEditorActivity.NewEditorActivityBuilder {

	RandomNewEditorActivityBuilder() {
		super.taskId(aRandom.nextLong())
				.endTime(TimeConverter.toJodaLocalDateTime(aRandom.dayOfYear()))
				.filePath(aRandom.filePath())
				.isModified(aRandom.coinFlip())
				.durationInSeconds(aRandom.tinyInt())
	}
}

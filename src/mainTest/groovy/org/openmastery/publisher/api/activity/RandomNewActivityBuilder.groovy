package org.openmastery.publisher.api.activity

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.task.NewTask

import static org.openmastery.publisher.ARandom.aRandom

class RandomNewActivityBuilder extends NewEditorActivity.NewEditorActivityBuilder {

	RandomNewActivityBuilder() {
		super.taskId(aRandom.nextLong())
				.filePath(aRandom.filePath())
				.isModified(aRandom.coinFlip())
				.durationInSeconds(aRandom.tinyInt())
	}
}

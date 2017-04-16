package org.openmastery.publisher.api.activity

import static org.openmastery.publisher.ARandom.aRandom

class RandomNewEditorActivityBuilder extends NewEditorActivity.NewEditorActivityBuilder {

	RandomNewEditorActivityBuilder() {
		super.taskId(aRandom.nextLong())
				.endTime(aRandom.dayOfYear())
				.filePath(aRandom.filePath())
				.isModified(aRandom.coinFlip())
				.durationInSeconds(aRandom.tinyInt())
	}
}

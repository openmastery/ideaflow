package org.openmastery.publisher.core.activity

import java.time.LocalDateTime

import static org.openmastery.publisher.ARandom.aRandom

class RandomEditorActivityEntityBuilder extends EditorActivityEntity.EditorActivityEntityBuilder {

	public RandomEditorActivityEntityBuilder() {
		LocalDateTime start = aRandom.dayOfYear()
		super.id(aRandom.id())
				.ownerId(aRandom.id())
				.taskId(aRandom.id())
				.start(start)
				.end(start.plus(aRandom.duration()))
				.filePath(aRandom.filePath())
				.isModified(aRandom.coinFlip())
	}

}

package org.openmastery.publisher.core.activity

import java.time.LocalDateTime

import static org.openmastery.publisher.ARandom.aRandom

class RandomModificationActivityEntityBuilder extends ModificationActivityEntity.ModificationActivityEntityBuilder {

	public RandomModificationActivityEntityBuilder() {
		LocalDateTime start = aRandom.dayOfYear()
		super.id(aRandom.id())
				.ownerId(aRandom.id())
				.taskId(aRandom.id())
				.start(start)
				.end(start.plus(aRandom.duration()))
				.modificationCount(aRandom.intBetween(0, 20))
	}

}

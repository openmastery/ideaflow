package org.openmastery.publisher.core.activity

import java.time.LocalDateTime

import static org.openmastery.publisher.ARandom.aRandom

class RandomIdleTimeBandEntityBuilder extends IdleTimeBandEntity.IdleTimeBandEntityBuilder {

	public RandomIdleTimeBandEntityBuilder() {
		LocalDateTime start = aRandom.dayOfYear()
		super.id(aRandom.id())
				.taskId(aRandom.id())
				.start(start)
				.end(start.plus(aRandom.duration()))
				.comment(aRandom.optionalWords(250))
				.auto(aRandom.coinFlip())
	}

}

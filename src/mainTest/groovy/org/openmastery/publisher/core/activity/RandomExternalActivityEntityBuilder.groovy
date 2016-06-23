package org.openmastery.publisher.core.activity

import java.time.LocalDateTime

import static org.openmastery.publisher.ARandom.aRandom

class RandomExternalActivityEntityBuilder extends ExternalActivityEntity.ExternalActivityEntityBuilder {

	public RandomExternalActivityEntityBuilder() {
		LocalDateTime start = aRandom.dayOfYear()
		super.id(aRandom.id())
				.taskId(aRandom.id())
				.start(start)
				.end(start.plus(aRandom.duration()))
				.comment(aRandom.text(20))
	}

}

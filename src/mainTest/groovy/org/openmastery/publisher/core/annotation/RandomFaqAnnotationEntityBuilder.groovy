package org.openmastery.publisher.core.annotation

import static org.openmastery.publisher.ARandom.aRandom

class RandomFaqAnnotationEntityBuilder extends FaqAnnotationEntity.FaqAnnotationEntityBuilder {
	RandomFaqAnnotationEntityBuilder() {
		super.id(aRandom.id())
				.ownerId(aRandom.id())
				.taskId(aRandom.id())
				.eventId(aRandom.id())
				.comment(aRandom.text(10))
	}
}

package org.openmastery.publisher.api.batch

import org.openmastery.publisher.api.event.EventType

import static org.openmastery.publisher.ARandom.aRandom

class RandomNewBatchEventBuilder extends NewBatchEvent.NewBatchEventBuilder {

	RandomNewBatchEventBuilder() {
		super.taskId(aRandom.nextLong())
				.type(EventType.AWESOME)
				.comment(aRandom.text(20))
				.position(aRandom.dayOfYear())
	}
}

package org.openmastery.publisher.api.event

import static org.openmastery.publisher.ARandom.aRandom

class RandomEventBuilder extends Event.EventBuilder {

	RandomEventBuilder() {
		super.id(aRandom.nextLong())
		.taskId(aRandom.nextLong())
		.position(aRandom.dayOfYear())
		.comment(aRandom.text(30))
		.type(EventType.NOTE)
	}

}

package org.openmastery.publisher.api.event

import org.openmastery.time.TimeConverter

import static org.openmastery.publisher.ARandom.aRandom

class RandomEventBuilder extends Event.EventBuilder {

	RandomEventBuilder() {
		super.id(aRandom.nextLong())
		.taskId(aRandom.nextLong())
		.position(TimeConverter.toJodaLocalDateTime(aRandom.dayOfYear()))
		.comment(aRandom.text(30))
		.type(EventType.NOTE)
	}
}

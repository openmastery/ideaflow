package org.openmastery.publisher.core.event

import org.openmastery.publisher.api.event.EventType

import static org.openmastery.publisher.ARandom.aRandom

class RandomEventEntityBuilder extends EventEntity.EventEntityBuilder {

	RandomEventEntityBuilder() {
		super.id(aRandom.id())
				.taskId(aRandom.id())
				.position(aRandom.dayOfYear())
				.comment(aRandom.optionalWords(250))
				.type(aRandom.item(EventType.values()))
	}

}

package org.ideaflow.publisher.core.event

import org.ideaflow.publisher.api.event.EventType

import static org.ideaflow.publisher.ARandom.aRandom

class RandomEventEntityBuilder extends EventEntity.EventEntityBuilder {

	RandomEventEntityBuilder() {
		super.id(aRandom.id())
				.taskId(aRandom.id())
				.position(aRandom.dayOfYear())
				.comment(aRandom.optionalWords(250))
				.eventType(aRandom.item(EventType.values()))
	}

}

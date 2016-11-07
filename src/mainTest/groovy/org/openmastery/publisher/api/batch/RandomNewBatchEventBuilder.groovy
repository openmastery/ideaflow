package org.openmastery.publisher.api.batch

import org.openmastery.publisher.ARandom
import org.openmastery.publisher.api.event.EventType
import org.openmastery.time.TimeConverter

class RandomNewBatchEventBuilder extends NewBatchEvent.NewBatchEventBuilder {

	RandomNewBatchEventBuilder() {
		super.taskId(ARandom.aRandom.nextLong())
				.type(EventType.AWESOME)
				.comment(ARandom.aRandom.text(20))
				.endTime(TimeConverter.toJodaLocalDateTime(ARandom.aRandom.dayOfYear()))
	}
}

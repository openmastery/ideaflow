package org.openmastery.publisher.core.activity

import java.time.LocalDateTime

import static org.openmastery.publisher.ARandom.aRandom

class RandomExecutionActivityEntityBuilder extends ExecutionActivityEntity.ExecutionActivityEntityBuilder {

	public RandomExecutionActivityEntityBuilder() {
		LocalDateTime start = aRandom.dayOfYear()
		super.id(aRandom.id())
				.ownerId(aRandom.id())
				.taskId(aRandom.id())
				.start(start)
				.end(start.plus(aRandom.duration()))
				.processName(aRandom.text(20))
				.exitCode(aRandom.intBetween(0, 255))
				.executionTaskType(aRandom.text(10))
				.debug(aRandom.coinFlip())
	}

}

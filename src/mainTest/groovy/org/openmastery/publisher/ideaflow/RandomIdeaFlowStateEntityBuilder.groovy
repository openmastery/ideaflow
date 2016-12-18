package org.openmastery.publisher.ideaflow

import java.time.LocalDateTime

import static org.openmastery.publisher.ARandom.aRandom

public class RandomIdeaFlowStateEntityBuilder extends IdeaFlowStateEntity.IdeaFlowStateEntityBuilder {

	RandomIdeaFlowStateEntityBuilder() {
		LocalDateTime start = aRandom.dayOfYear()
		super.id(aRandom.id())
				.ownerId(aRandom.id())
				.taskId(aRandom.id())
				.start(start)
				.end(start.plus(aRandom.duration()))
				.startingComment(aRandom.optionalWords(250))
				.endingComment(aRandom.optionalWords(250))
				.isLinkedToPrevious(aRandom.coinFlip())
				.isNested(aRandom.coinFlip())
	}

}

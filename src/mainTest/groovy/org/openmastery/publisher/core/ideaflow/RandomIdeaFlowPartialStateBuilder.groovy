package org.openmastery.publisher.core.ideaflow

import static org.openmastery.publisher.ARandom.aRandom

public class RandomIdeaFlowPartialStateBuilder extends IdeaFlowPartialStateEntity.IdeaFlowPartialStateEntityBuilder {

	RandomIdeaFlowPartialStateBuilder() {
		super.ownerId(aRandom.id())
				.taskId(aRandom.id())
				.start(aRandom.dayOfYear())
				.startingComment(aRandom.optionalWords(250))
				.isLinkedToPrevious(aRandom.coinFlip())
				.isNested(aRandom.coinFlip())
	}

}

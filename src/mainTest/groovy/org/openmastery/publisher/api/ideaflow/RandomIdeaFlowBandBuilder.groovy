package org.openmastery.publisher.api.ideaflow

import static org.openmastery.publisher.ARandom.aRandom

class RandomIdeaFlowBandBuilder extends IdeaFlowBand.IdeaFlowBandBuilder {

	RandomIdeaFlowBandBuilder() {
		super.id(aRandom.nextLong())
				.taskId(aRandom.nextLong())
				.startingComment(aRandom.text(30))
				.endingComent(aRandom.text(30))
				.type(IdeaFlowStateType.PROGRESS)
				.start(aRandom.dateInPastDays(30).atStartOfDay())
				.end(aRandom.dateInFuture(30).atStartOfDay())
				.durationInSeconds(aRandom.intBetween(1, 1000))
				.relativePositionInSeconds(aRandom.intBetween(1, 1000))
	}


}

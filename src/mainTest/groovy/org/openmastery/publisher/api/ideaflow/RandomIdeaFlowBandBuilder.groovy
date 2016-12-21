package org.openmastery.publisher.api.ideaflow

import org.openmastery.time.TimeConverter

import static org.openmastery.publisher.ARandom.aRandom

class RandomIdeaFlowBandBuilder extends IdeaFlowBand.IdeaFlowBandBuilder {

	RandomIdeaFlowBandBuilder() {
		super.id(aRandom.nextLong())
				.taskId(aRandom.nextLong())
				.startingComment(aRandom.text(30))
				.endingComent(aRandom.text(30))
				.type(IdeaFlowStateType.PROGRESS)
				.start(TimeConverter.toJodaLocalDateTime(aRandom.dateInPastDays(30).atStartOfDay()))
				.end(TimeConverter.toJodaLocalDateTime(aRandom.dateInFuture(30).atStartOfDay()))
				.durationInSeconds(aRandom.intBetween(1, 1000))
				.relativePositionInSeconds(aRandom.intBetween(1, 1000))
	}


}

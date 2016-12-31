package org.openmastery.publisher.api.ideaflow

import org.joda.time.LocalDateTime


class IdeaFlowBandValidator {

	private IdeaFlowBand ideaFlowBand

	IdeaFlowBandValidator(IdeaFlowBand ideaFlowBand) {
		this.ideaFlowBand = ideaFlowBand
	}

	IdeaFlowBandValidator assertExpectedValues(IdeaFlowStateType expectedType, LocalDateTime expectedStartTime, LocalDateTime expectedEndTime) {
		assert ideaFlowBand.type == expectedType
		assert ideaFlowBand.start == expectedStartTime
		assert ideaFlowBand.end == expectedEndTime
		this
	}

	IdeaFlowBandValidator assertRelativePositionInSeconds(Long expectedRelativePositionInSeconds) {
		assert ideaFlowBand.relativePositionInSeconds == expectedRelativePositionInSeconds
		this
	}

	IdeaFlowBandValidator assertDurationInSeconds(Long expectedDurationInSeconds) {
		assert ideaFlowBand.durationInSeconds == expectedDurationInSeconds
		this
	}

}

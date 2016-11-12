package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.timeline.BandTimeline
import org.openmastery.publisher.api.timeline.TimeBandGroup
import org.openmastery.publisher.api.timeline.TreeNode
import org.openmastery.publisher.api.timeline.TreeNodeType
import org.openmastery.publisher.api.timeline.TreeTimeline

import java.time.Duration

import static IdeaFlowStateType.PROGRESS

class TimelineValidator {

	private BandTimeline bandTimeline
	private int ideaFlowBandIndex = 0
	private int timeBandGroupIndex = 0
	private int eventIndex = 0
	private int treeNodeIndex = 0

	private IdeaFlowBand activeTimeBand
	private List<IdeaFlowBand> activeNestedTimeBands = []
	private List<IdeaFlowBand> activeLinkedTimeBands = []

	TimelineValidator(BandTimeline bandTimeline) {
		this.bandTimeline = bandTimeline

	}


	private void assertTimeBandValues(IdeaFlowBand actualBand, IdeaFlowStateType expectedType, Duration expectedDuration,
	                                  String startingComment, String endingComment) {
		assert actualBand.type == expectedType
		assert actualBand.durationInSeconds == expectedDuration.seconds
		assert actualBand.relativePositionInSeconds != null
		assert actualBand.startingComment == startingComment
		assert actualBand.endingComent == endingComment
		if (ideaFlowBandIndex > 1) {
			assert actualBand.relativePositionInSeconds > 0
		}
	}

	private void assertNoActiveLinkedOrNestedTimeBands() {
		assert activeNestedTimeBands.isEmpty()
		assert activeLinkedTimeBands.isEmpty()
	}

	void assertValidationComplete() {
		assert bandTimeline.ideaFlowBands.size() == ideaFlowBandIndex
		assert bandTimeline.timeBandGroups.size() == timeBandGroupIndex
		assert bandTimeline.events.size() == eventIndex
	}


	void assertSegmentStart(String expectedComment) {
		assertEvent(EventType.SUBTASK, expectedComment)
	}

	void assertSegmentStartAndProgressNode(Duration expectedDuration, String expectedComment) {
		assertSegmentStart(expectedComment)
	}

	void assertEvent(EventType expectedEventType, String expectedComment) {
		Event event = bandTimeline.events[eventIndex++]
		assert event.type == expectedEventType
		assert event.relativePositionInSeconds != null
		assert event.comment == expectedComment
		if (eventIndex > 1) {
			assert event.relativePositionInSeconds > 0
		}
	}

	void assertIdeaFlowBand(IdeaFlowStateType expectedType, Duration expectedDuration, String startingComment = null, String endingComment = null) {
		assertNoActiveLinkedOrNestedTimeBands()

		IdeaFlowBand ideaFlowBand = bandTimeline.ideaFlowBands[ideaFlowBandIndex++]
		assertTimeBandValues(ideaFlowBand, expectedType, expectedDuration, startingComment, endingComment)
		activeTimeBand = ideaFlowBand
		activeNestedTimeBands = [] + ideaFlowBand.nestedBands
	}


	void assertNestedTimeBand(IdeaFlowStateType expectedType, Duration expectedDuration, String startingComment, String endingComment) {
		assert activeLinkedTimeBands.isEmpty()
		assert activeNestedTimeBands.isEmpty() == false
		IdeaFlowBand ideaFlowBand = activeNestedTimeBands.remove(0)
		assertTimeBandValues(ideaFlowBand, expectedType, expectedDuration, startingComment, endingComment)
	}

	void assertLinkedBand(IdeaFlowStateType expectedType, Duration expectedDuration, String startingComment, String endingComment) {
		assert activeNestedTimeBands.isEmpty()

		if (activeLinkedTimeBands.isEmpty()) {
			assert bandTimeline.timeBandGroups[timeBandGroupIndex] != null
			TimeBandGroup timeBandGroup = bandTimeline.timeBandGroups[timeBandGroupIndex++]
			activeLinkedTimeBands = timeBandGroup.linkedTimeBands
			assert timeBandGroup.relativePositionInSeconds != null
			if (timeBandGroupIndex > 1) {
				assert timeBandGroup.relativePositionInSeconds > 0
			}
		}
		assert activeLinkedTimeBands.isEmpty() == false
		IdeaFlowBand ideaFlowBand = activeLinkedTimeBands.remove(0)
		assertTimeBandValues(ideaFlowBand, expectedType, expectedDuration, startingComment, endingComment)
		activeNestedTimeBands = [] + ideaFlowBand.nestedBands

	}

	void assertLinkedNestedTimeBand(IdeaFlowStateType expectedType, Duration expectedDuration, String startingComment, String endingComment) {
		assert activeNestedTimeBands.isEmpty() == false
		IdeaFlowBand ideaFlowBand = activeNestedTimeBands.remove(0)
		assertTimeBandValues(ideaFlowBand, expectedType, expectedDuration, startingComment, endingComment)
	}

}

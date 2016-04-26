package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowStateType
import org.ideaflow.publisher.api.IdeaFlowBand
import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.api.TimeBandGroup
import org.ideaflow.publisher.api.TimelineSegment

import java.time.Duration


class TimelineSegmentValidator {

	private int expectedTimeBandCount = 0
	private int expectedNestedTimeBandCount = 0
	private int expectedLinkedTimeBandCount = 0

	private void assertExpectedValues(List<IdeaFlowBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assert timeBands[index].type == expectedType
		assert timeBands[index].duration == expectedDuration
	}

	void assertTimeBand(List<IdeaFlowBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration)
		expectedTimeBandCount++
	}

	void assertNestedTimeBand(List<IdeaFlowBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration)
		expectedNestedTimeBandCount++
	}

	void assertLinkedTimeBand(List<IdeaFlowBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration)
		expectedLinkedTimeBandCount++
	}

	void assertValidationComplete(TimelineSegment segment) {
		assertValidationComplete([segment], 1)
	}

	void assertValidationComplete(List<TimelineSegment> segments, int expectedSegmentCount) {
		assert expectedTimeBandCount == (segments.sum { it.ideaFlowBands.size() } as int)
		assert expectedLinkedTimeBandCount == (segments.sum { countLinkedTimeBands(it) } as int)
		assert expectedNestedTimeBandCount == (segments.sum { countNestedBands(it) } as int)
		assert expectedSegmentCount == segments.size()
	}

	private int countLinkedTimeBands(TimelineSegment segment) {
		int linkedTimeBandCount = 0
		segment.timeBandGroups.each { TimeBandGroup group ->
			linkedTimeBandCount += group.linkedTimeBands.size()
		}
		linkedTimeBandCount
	}

	private int countNestedBands(TimelineSegment segment) {
		int nestedBandCount = sumNestedTimeBands(segment.ideaFlowBands)
		segment.timeBandGroups.each { TimeBandGroup group ->
			nestedBandCount += sumNestedTimeBands(group.linkedTimeBands)
		}
		nestedBandCount
	}

	private int sumNestedTimeBands(List<IdeaFlowBand> timeBands) {
		timeBands.size() == 0 ? 0 : timeBands.sum { IdeaFlowBand timeBand -> timeBand.nestedBands.size() } as int
	}

}

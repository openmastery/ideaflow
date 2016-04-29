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

	private void assertExpectedValues(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration, Duration.ZERO)
	}

	private void assertExpectedValues(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration, Duration expectedIdle) {
		assert timeBands[index] != null
		if (timeBands[index] instanceof IdeaFlowBand) {
			assert ((IdeaFlowBand) timeBands[index]).type == expectedType
			assert ((IdeaFlowBand) timeBands[index]).idleDuration == expectedIdle
		}
		assert timeBands[index].duration == expectedDuration
	}

	void assertTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration)
		expectedTimeBandCount++
	}

	void assertTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration, Duration expectedIdle) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration, expectedIdle)
		expectedTimeBandCount++
	}

	void assertNestedTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration)
		expectedNestedTimeBandCount++
	}

	void assertNestedTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration, Duration expectedIdle) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration, expectedIdle)
		expectedNestedTimeBandCount++
	}

	void assertLinkedTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration)
		expectedLinkedTimeBandCount++
	}

	void assertLinkedTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration, Duration expectedIdle) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration, expectedIdle)
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

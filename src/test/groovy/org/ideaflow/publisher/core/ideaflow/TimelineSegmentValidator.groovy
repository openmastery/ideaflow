package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowStateType
import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.api.TimeBandGroup
import org.ideaflow.publisher.api.TimelineSegment

import java.time.Duration


class TimelineSegmentValidator {

	private int expectedTimeBandCount = 0
	private int expectedNestedTimeBandCount = 0
	private int expectedLinkedTimeBandCount = 0

	private void assertExpectedValues(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assert timeBands[index].type == expectedType
		assert timeBands[index].duration == expectedDuration
	}

	void assertTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration)
		expectedTimeBandCount++
	}

	void assertNestedTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration)
		expectedNestedTimeBandCount++
	}

	void assertLinkedTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration)
		expectedLinkedTimeBandCount++
	}

	void assertValidationComplete(TimelineSegment segment) {
		assert expectedTimeBandCount == segment.timeBands.size()
		assert expectedLinkedTimeBandCount == countLinkedTimeBands(segment)
		assert expectedNestedTimeBandCount == countNestedBands(segment)
	}

	private int countLinkedTimeBands(TimelineSegment segment) {
		int linkedTimeBandCount = 0
		segment.timeBandGroups.each { TimeBandGroup group ->
			linkedTimeBandCount += group.linkedTimeBands.size()
		}
		linkedTimeBandCount
	}

	private int countNestedBands(TimelineSegment segment) {
		int nestedBandCount = sumNestedTimeBands(segment.timeBands)
		segment.timeBandGroups.each { TimeBandGroup group ->
			nestedBandCount += sumNestedTimeBands(group.linkedTimeBands)
		}
		nestedBandCount
	}

	private int sumNestedTimeBands(List<TimeBand> timeBands) {
		timeBands.sum { TimeBand timeBand -> timeBand.nestedBands.size() } as int
	}

}

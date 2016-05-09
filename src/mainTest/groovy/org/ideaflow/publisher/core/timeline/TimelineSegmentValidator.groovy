package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.event.EventType
import org.ideaflow.publisher.api.ideaflow.IdeaFlowBand
import org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType
import org.ideaflow.publisher.api.timeline.TimeBand
import org.ideaflow.publisher.api.timeline.TimeBandGroup
import org.ideaflow.publisher.api.timeline.TimelineSegment

import java.time.Duration
import java.time.LocalDateTime

class TimelineSegmentValidator {

	private int expectedTimeBandCount = 0
	private int expectedNestedTimeBandCount = 0
	private int expectedLinkedTimeBandCount = 0
	private int expectedEventCount = 0

	private void assertExpectedValues(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType,
	                                  Duration expectedDuration, Long expectedRelativeStart) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration, Duration.ZERO, expectedRelativeStart)
	}

	private void assertExpectedValues(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType,
	                                  Duration expectedDuration, Duration expectedIdle, Long expectedRelativeStart) {
		assert timeBands[index] != null
		if (timeBands[index] instanceof IdeaFlowBand) {
			assert ((IdeaFlowBand) timeBands[index]).type == expectedType
			assert ((IdeaFlowBand) timeBands[index]).idleDuration == expectedIdle
		}
		assert timeBands[index].duration == expectedDuration
		if (expectedRelativeStart != null) {
			assert timeBands[index].relativeStart == expectedRelativeStart
		}
	}

	void assertEvent(TimelineSegment segment, int index, EventType expectedType, LocalDateTime expectedPosition) {
		assert segment.events[index] != null
		assert segment.events[index].eventType == expectedType
		assert segment.events[index].position == expectedPosition
		expectedEventCount++
	}

	void assertTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration,
	                    Long expectedRelativeStart = null) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration, expectedRelativeStart)
		expectedTimeBandCount++
	}

	void assertTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration,
	                    Duration expectedIdle, Long expectedRelativeStart = null) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration, expectedIdle, expectedRelativeStart)
		expectedTimeBandCount++
	}

	void assertNestedTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration,
	                          Long expectedRelativeStart = null) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration, expectedRelativeStart)
		expectedNestedTimeBandCount++
	}

	void assertNestedTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration,
	                          Duration expectedIdle, Long expectedRelativeStart = null) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration, expectedIdle, expectedRelativeStart)
		expectedNestedTimeBandCount++
	}

	void assertLinkedTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType,
	                          Duration expectedDuration, Long expectedRelativeStart = null) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration, expectedRelativeStart)
		expectedLinkedTimeBandCount++
	}

	void assertLinkedTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType,
	                          Duration expectedDuration, Duration expectedIdle, Long expectedRelativeStart = null) {
		assertExpectedValues(timeBands, index, expectedType, expectedDuration, expectedIdle, expectedRelativeStart)
		expectedLinkedTimeBandCount++
	}

	void assertValidationComplete(TimelineSegment segment) {
		assertValidationComplete([segment], 1)
	}

	void assertValidationComplete(List<TimelineSegment> segments, int expectedSegmentCount) {
		assert expectedTimeBandCount == (segments.sum { it.ideaFlowBands.size() } as int)
		assert expectedLinkedTimeBandCount == (segments.sum { countLinkedTimeBands(it) } as int)
		assert expectedNestedTimeBandCount == (segments.sum { countNestedBands(it) } as int)
		assert expectedEventCount == (segments.sum { it.events.size() } as int)
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

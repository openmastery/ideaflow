package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.EventType
import org.ideaflow.publisher.api.Timeline
import org.ideaflow.publisher.api.TimelineSegment
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.ideaflow.publisher.api.IdeaFlowStateType.CONFLICT
import static org.ideaflow.publisher.api.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.IdeaFlowStateType.PROGRESS
import static org.ideaflow.publisher.api.IdeaFlowStateType.REWORK

class TimelineGeneratorSpec extends Specification {

	TimelineSegmentValidator validator = new TimelineSegmentValidator()
	TimelineTestSupport testSupport = new TimelineTestSupport()
	LocalDateTime start

	def setup() {
		start = testSupport.now()
		testSupport.startTaskAndAdvanceHours(1)
	}

	private Timeline createTimeline() {
		new TimelineGenerator().createTimeline(
				testSupport.stateListWithActiveCompleted,
				testSupport.idleActivityList,
				testSupport.eventList
		)
	}

	def "WHEN subtask splits between idle bands SHOULD split idle time between the time segments"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 2)
		testSupport.idle(3)
		testSupport.startSubtaskAndAdvanceHours(1)
		testSupport.idle(2)
		testSupport.endBand(LEARNING)

		when:
		Timeline timeline = createTimeline()

		then:
		List<TimelineSegment> segments = timeline.timelineSegments
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1), 0)
		validator.assertTimeBand(segments[0].ideaFlowBands, 1, LEARNING, Duration.ofHours(2), Duration.ofHours(3), Duration.ofHours(1).seconds)
		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusHours(6))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, LEARNING, Duration.ofHours(1), Duration.ofHours(2), Duration.ofHours(3).seconds)
		validator.assertTimeBand(segments[1].ideaFlowBands, 1, PROGRESS, Duration.ZERO, Duration.ofHours(4).seconds)
		validator.assertValidationComplete(segments, 2)
	}

	def "WHEN subtask splits linked bands SHOULD include idle"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 2)
		testSupport.idle(1)
		testSupport.startSubtaskAndAdvanceHours(1)
		testSupport.startBandAndAdvanceHours(REWORK, 3)
		testSupport.idle(2)

		when:
		Timeline timeline = createTimeline()

		then:
		List<TimelineSegment> segments = timeline.timelineSegments
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1), 0)
		validator.assertLinkedTimeBand(segments[0].timeBandGroups[0].linkedTimeBands, 0, LEARNING, Duration.ofHours(2), Duration.ofHours(1), Duration.ofHours(1).seconds)
		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusHours(4))
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 0, LEARNING, Duration.ofHours(1), Duration.ofHours(3).seconds)
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 1, REWORK, Duration.ofHours(3), Duration.ofHours(2), Duration.ofHours(4).seconds)
		validator.assertValidationComplete(segments, 2)
	}

	def "SHOULD handle nested bands within linked bands"() {
		given:
		testSupport.startBandAndAdvanceHours(CONFLICT, 1)
		testSupport.idle(1)
		testSupport.startBandAndAdvanceHours(LEARNING, 2)
		testSupport.idle(2)
		testSupport.advanceHours(1)
		testSupport.startBandAndAdvanceHours(CONFLICT, 1)
		testSupport.idle(1)
		testSupport.startSubtaskAndAdvanceHours(2)

		when:
		Timeline timeline = createTimeline()

		then:
		List<TimelineSegment> segments = timeline.timelineSegments
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1), 0)
		validator.assertLinkedTimeBand(segments[0].timeBandGroups[0].linkedTimeBands, 0, CONFLICT, Duration.ofHours(1), Duration.ofHours(1), Duration.ofHours(1).seconds)
		validator.assertLinkedTimeBand(segments[0].timeBandGroups[0].linkedTimeBands, 1, LEARNING, Duration.ofHours(4), Duration.ofHours(3), Duration.ofHours(2).seconds)
		validator.assertNestedTimeBand(segments[0].timeBandGroups[0].linkedTimeBands[1].nestedBands, 0, CONFLICT, Duration.ofHours(1), Duration.ofHours(1), Duration.ofHours(5).seconds)
		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusHours(10))
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 0, LEARNING, Duration.ofHours(2), Duration.ofHours(6).seconds)
		validator.assertNestedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands[0].nestedBands, 0, CONFLICT, Duration.ofHours(2), Duration.ofHours(6).seconds)
		validator.assertValidationComplete(segments, 2)
	}

}

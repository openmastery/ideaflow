package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.timeline.TimeBand
import org.ideaflow.publisher.core.timeline.BandTimelineSegment
import org.ideaflow.publisher.core.timeline.TimelineSegmentValidator
import org.ideaflow.publisher.core.timeline.TimelineTestSupport
import org.ideaflow.publisher.core.timeline.IdleTimeProcessor
import org.ideaflow.publisher.core.timeline.BandTimelineSegmentFactory
import spock.lang.Specification

import java.time.Duration

import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.CONFLICT
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.REWORK

class IdleTimeProcessorSpec extends Specification {

	TimelineSegmentValidator validator = new TimelineSegmentValidator()
	TimelineTestSupport testSupport = new TimelineTestSupport()

	def setup() {
		testSupport.startTaskAndAdvanceHours(1)
	}

	private BandTimelineSegment createTimelineSegmentAndParseIdleTime() {
		List<IdeaFlowStateEntity> stateList = testSupport.getStateListWithActiveCompleted()

		BandTimelineSegmentFactory segmentFactory = new BandTimelineSegmentFactory()
		BandTimelineSegment segment = segmentFactory.createTimelineSegment(stateList, testSupport.getEventList())

		IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
		idleTimeProcessor.collapseIdleTime(segment, testSupport.getIdleActivityList())
		segment
	}

	def "WHEN idle time is within a Timeband SHOULD subtract relative time from band"() {
		given:
		testSupport.startBandAndAdvanceHours(CONFLICT, 1)
		testSupport.endBandAndAdvanceHours(CONFLICT, 2)
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.idle(3)
		testSupport.advanceHours(1)

		when:
		BandTimelineSegment segment = createTimelineSegmentAndParseIdleTime()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segment.ideaFlowBands, 1, CONFLICT, Duration.ofHours(1))
		validator.assertTimeBand(segment.ideaFlowBands, 2, PROGRESS, Duration.ofHours(2))
		validator.assertTimeBand(segment.ideaFlowBands, 3, LEARNING, Duration.ofHours(2), Duration.ofHours(3))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(6)
	}

	def "WHEN idle time is within a nested Timeband SHOULD subtract relative time from parent and child band"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.startBandAndAdvanceHours(CONFLICT, 2)
		testSupport.idle(4)
		testSupport.advanceHours(1)

		when:
		BandTimelineSegment segment = createTimelineSegmentAndParseIdleTime()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segment.ideaFlowBands, 1, LEARNING, Duration.ofHours(4), Duration.ofHours(4))
		List nestedBands = segment.ideaFlowBands[1].nestedBands
		validator.assertNestedTimeBand(nestedBands, 0, CONFLICT, Duration.ofHours(3), Duration.ofHours(4))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(5)
	}

	def "WHEN multiple idles within band SHOULD provide total idle duration"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.idle(2)
		testSupport.advanceHours(3)
		testSupport.idle(4)

		when:
		BandTimelineSegment segment = createTimelineSegmentAndParseIdleTime()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segment.ideaFlowBands, 1, LEARNING, Duration.ofHours(4), Duration.ofHours(6))
		validator.assertValidationComplete(segment)
	}

	def "WHEN idle part of group SHOULD split idle among linked time bands"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 2)
		testSupport.idle(2)
		testSupport.advanceHours(2)
		testSupport.startBandAndAdvanceHours(REWORK, 3)
		testSupport.advanceHours(3)
		testSupport.idle(3)

		when:
		BandTimelineSegment segment = createTimelineSegmentAndParseIdleTime()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		List<TimeBand> linkedTimeBands = segment.timeBandGroups[0].linkedTimeBands
		validator.assertLinkedTimeBand(linkedTimeBands, 0, LEARNING, Duration.ofHours(4), Duration.ofHours(2))
		validator.assertLinkedTimeBand(linkedTimeBands, 1, REWORK, Duration.ofHours(6), Duration.ofHours(3))
		validator.assertValidationComplete(segment)
	}

}

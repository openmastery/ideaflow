package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.TimelineSegment
import spock.lang.Specification

import java.time.Duration

import static org.ideaflow.publisher.api.IdeaFlowStateType.CONFLICT
import static org.ideaflow.publisher.api.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.IdeaFlowStateType.PROGRESS

class IdleTimeProcessorSpec extends Specification {

	TimelineSegmentValidator validator = new TimelineSegmentValidator()
	TimelineTestSupport testSupport = new TimelineTestSupport()

	def setup() {
		testSupport.startTaskAndAdvanceHours(1)
	}

	private TimelineSegment createTimelineSegmentAndParseIdleTime() {
		List<IdeaFlowStateEntity> stateList = testSupport.getStateListWithActiveCompleted()

		TimelineSegmentFactory segmentFactory = new TimelineSegmentFactory()
		TimelineSegment segment = segmentFactory.createTimelineSegment(stateList)

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
		TimelineSegment segment = createTimelineSegmentAndParseIdleTime()

		then:
		validator.assertTimeBand(segment.timeBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segment.timeBands, 1, CONFLICT, Duration.ofHours(1))
		validator.assertTimeBand(segment.timeBands, 2, PROGRESS, Duration.ofHours(2))
		validator.assertTimeBand(segment.timeBands, 3, LEARNING, Duration.ofHours(2))
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
		TimelineSegment segment = createTimelineSegmentAndParseIdleTime()

		then:
		validator.assertTimeBand(segment.timeBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segment.timeBands, 1, LEARNING, Duration.ofHours(4))
		List nestedBands = segment.timeBands[1].nestedBands
		validator.assertNestedTimeBand(nestedBands, 0, CONFLICT, Duration.ofHours(3))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(5)
	}

}

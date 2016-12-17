package org.openmastery.publisher.core.ideaflow

import org.joda.time.Duration
import org.openmastery.publisher.core.timeline.BandTimelineSegmentBuilder
import org.openmastery.publisher.core.timeline.TimeBandModel
import org.openmastery.publisher.core.timeline.BandTimelineSegment
import org.openmastery.publisher.core.timeline.TimelineSegmentValidator
import org.openmastery.publisher.core.timeline.IdleTimeProcessor
import org.openmastery.publisher.core.timeline.TimelineTestSupport
import spock.lang.Specification

import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.TROUBLESHOOTING
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.LEARNING
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.REWORK

class IdleTimeProcessorSpec extends Specification {

	TimelineSegmentValidator validator = new TimelineSegmentValidator()
	TimelineTestSupport testSupport = new TimelineTestSupport()

	def setup() {
		testSupport.startTaskAndAdvanceHours(1)
	}

	private BandTimelineSegment createTimelineSegmentAndParseIdleTime() {
		List<IdeaFlowStateEntity> stateList = testSupport.getStateListWithActiveCompleted()

		BandTimelineSegment segment = new BandTimelineSegmentBuilder(stateList)
				.events(testSupport.getEventList())
				.build()

		IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
		idleTimeProcessor.collapseIdleTime(segment, testSupport.getIdleActivityList())
		segment
	}

	def "WHEN idle time is within a Timeband SHOULD subtract relative time from band"() {
		given:
		testSupport.startBandAndAdvanceHours(TROUBLESHOOTING, 1)
		testSupport.endBandAndAdvanceHours(TROUBLESHOOTING, 2)
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.idle(3)
		testSupport.advanceHours(1)

		when:
		BandTimelineSegment segment = createTimelineSegmentAndParseIdleTime()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.standardHours(1))
		validator.assertTimeBand(segment.ideaFlowBands, 1, TROUBLESHOOTING, Duration.standardHours(1))
		validator.assertTimeBand(segment.ideaFlowBands, 2, PROGRESS, Duration.standardHours(2))
		validator.assertTimeBand(segment.ideaFlowBands, 3, LEARNING, Duration.standardHours(2), Duration.standardHours(3))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.standardHours(6)
	}

	def "WHEN idle time is within a nested Timeband SHOULD subtract relative time from parent and child band"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.startBandAndAdvanceHours(TROUBLESHOOTING, 2)
		testSupport.idle(4)
		testSupport.advanceHours(1)

		when:
		BandTimelineSegment segment = createTimelineSegmentAndParseIdleTime()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.standardHours(1))
		validator.assertTimeBand(segment.ideaFlowBands, 1, LEARNING, Duration.standardHours(4), Duration.standardHours(4))
		List nestedBands = segment.ideaFlowBands[1].nestedBands
		validator.assertNestedTimeBand(nestedBands, 0, TROUBLESHOOTING, Duration.standardHours(3), Duration.standardHours(4))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.standardHours(5)
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
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.standardHours(1))
		validator.assertTimeBand(segment.ideaFlowBands, 1, LEARNING, Duration.standardHours(4), Duration.standardHours(6))
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
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.standardHours(1))
		List<TimeBandModel> linkedTimeBands = segment.timeBandGroups[0].linkedTimeBands
		validator.assertLinkedTimeBand(linkedTimeBands, 0, LEARNING, Duration.standardHours(4), Duration.standardHours(2))
		validator.assertLinkedTimeBand(linkedTimeBands, 1, REWORK, Duration.standardHours(6), Duration.standardHours(3))
		validator.assertValidationComplete(segment)
	}

}

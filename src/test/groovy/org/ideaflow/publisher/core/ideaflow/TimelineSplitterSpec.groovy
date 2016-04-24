package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.TimelineSegment
import spock.lang.Specification

import java.time.Duration

import static org.ideaflow.publisher.api.IdeaFlowStateType.CONFLICT
import static org.ideaflow.publisher.api.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.IdeaFlowStateType.PROGRESS

class TimelineSplitterSpec extends Specification {

	TimelineSegmentValidator validator = new TimelineSegmentValidator()
	TimelineTestSupport testSupport = new TimelineTestSupport()

	def setup() {
		testSupport.startTaskAndAdvanceHours(1)
	}

	private List<TimelineSegment> createTimelineSegmentAndSplit() {
		List<IdeaFlowStateEntity> stateList = testSupport.getStateListWithActiveCompleted()

		TimelineSegmentFactory segmentFactory = new TimelineSegmentFactory()
		TimelineSegment segment = segmentFactory.createTimelineSegment(stateList)

		TimelineSplitter splitter = new TimelineSplitter()
		splitter.splitTimelineSegment(segment, testSupport.getEventList())
	}

	def "WHEN subtask start is within Timeband SHOULD split timeband across TimelineSegments"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 2)
		testSupport.startSubtaskAndAdvanceHours(3)

		when:
		List<TimelineSegment> segments = createTimelineSegmentAndSplit()

		then:
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segments[0].ideaFlowBands, 1, LEARNING, Duration.ofHours(2))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, LEARNING, Duration.ofHours(3))
		validator.assertValidationComplete(segments, 2)
	}

	def "WHEN multiple subtask starts within a single TimeBand SHOULD split into multiple TimelineSegments"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 2)
		testSupport.startSubtaskAndAdvanceHours(3)
		testSupport.startSubtaskAndAdvanceHours(4)

		when:
		List<TimelineSegment> segments = createTimelineSegmentAndSplit()

		then:
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segments[0].ideaFlowBands, 1, LEARNING, Duration.ofHours(2))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, LEARNING, Duration.ofHours(3))
		validator.assertTimeBand(segments[2].ideaFlowBands, 0, LEARNING, Duration.ofHours(4))
		validator.assertValidationComplete(segments, 3)
	}

	def "WHEN subtask position at boundary TimeBand SHOULD not split any TimeBand"() {
		given:
		testSupport.startBand(LEARNING)
		testSupport.startSubtaskAndAdvanceHours(2)

		when:
		List<TimelineSegment> segments = createTimelineSegmentAndSplit()

		then:
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, LEARNING, Duration.ofHours(2))
		validator.assertValidationComplete(segments, 2)
	}

	def "WHEN subtask start is within nested Timeband SHOULD split containing and nested bands across TimelineSegments"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 2)
		testSupport.startBandAndAdvanceHours(CONFLICT, 1)
		testSupport.endBandAndAdvanceHours(CONFLICT, 1)
		testSupport.startBandAndAdvanceHours(CONFLICT, 2)
		testSupport.startSubtaskAndAdvanceHours(1)

		when:
		List<TimelineSegment> segments = createTimelineSegmentAndSplit()

		then:
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segments[0].ideaFlowBands, 1, LEARNING, Duration.ofHours(6))
		validator.assertNestedTimeBand(segments[0].ideaFlowBands[1].nestedBands, 0, CONFLICT, Duration.ofHours(1))
		validator.assertNestedTimeBand(segments[0].ideaFlowBands[1].nestedBands, 1, CONFLICT, Duration.ofHours(2))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, LEARNING, Duration.ofHours(1))
		validator.assertNestedTimeBand(segments[1].ideaFlowBands[0].nestedBands, 0, CONFLICT, Duration.ofHours(1))
		validator.assertValidationComplete(segments, 2)
	}

	def "WHEN subtask is between two nested Timebands SHOULD split containing and nested bands across TimelineSegments"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 2)
		testSupport.startBandAndAdvanceHours(CONFLICT, 1)
		testSupport.endBandAndAdvanceHours(CONFLICT, 1)
		testSupport.startSubtaskAndAdvanceHours(1)
		testSupport.startBandAndAdvanceHours(CONFLICT, 2)

		when:
		List<TimelineSegment> segments = createTimelineSegmentAndSplit()

		then:
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segments[0].ideaFlowBands, 1, LEARNING, Duration.ofHours(4))
		validator.assertNestedTimeBand(segments[0].ideaFlowBands[1].nestedBands, 0, CONFLICT, Duration.ofHours(1))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, LEARNING, Duration.ofHours(3))
		validator.assertNestedTimeBand(segments[1].ideaFlowBands[0].nestedBands, 0, CONFLICT, Duration.ofHours(2))
		validator.assertValidationComplete(segments, 2)
	}

}

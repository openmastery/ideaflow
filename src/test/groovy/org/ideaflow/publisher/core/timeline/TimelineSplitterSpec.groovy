package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.event.EventType
import org.ideaflow.publisher.api.timeline.TimelineSegment
import org.ideaflow.publisher.core.activity.EditorActivityEntity
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateEntity
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.CONFLICT
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.REWORK

class TimelineSplitterSpec extends Specification {

	TimelineSegmentValidator validator = new TimelineSegmentValidator()
	TimelineTestSupport testSupport = new TimelineTestSupport()
	TimelineSegment inputSegment
	LocalDateTime start

	def setup() {
		start = testSupport.now()
		testSupport.startTaskAndAdvanceHours(1)
	}

	private List<TimelineSegment> createTimelineSegmentAndSplit() {
		List<IdeaFlowStateEntity> stateList = testSupport.getStateListWithActiveCompleted()

		TimelineSegmentFactory segmentFactory = new TimelineSegmentFactory()
		inputSegment = segmentFactory.createTimelineSegment(stateList, testSupport.getEventList())
		inputSegment.description = "initial segment"

		TimelineSplitter splitter = new TimelineSplitter()
		splitter.splitTimelineSegment(inputSegment)
	}

	def "WHEN not split SHOULD return input segment"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 2)
		testSupport.startBandAndAdvanceHours(REWORK, 1)

		when:
		List<TimelineSegment> actualSegments = createTimelineSegmentAndSplit()

		then:
		assert inputSegment.is(actualSegments[0])
		assert actualSegments.size() == 1
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
		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusHours(3))
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
		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusHours(3))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, LEARNING, Duration.ofHours(3))
		validator.assertEvent(segments[2], 0, EventType.SUBTASK, start.plusHours(6))
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
		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusHours(1))
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
		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusHours(7))
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
		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusHours(5))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, LEARNING, Duration.ofHours(3))
		validator.assertNestedTimeBand(segments[1].ideaFlowBands[0].nestedBands, 0, CONFLICT, Duration.ofHours(2))
		validator.assertValidationComplete(segments, 2)
	}

	def "WHEN subtask splits linked bands SHOULD split across TimelineSegments"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 2)
		testSupport.startBandAndAdvanceHours(REWORK, 3)
		testSupport.startSubtaskAndAdvanceHours(4)
		testSupport.startBandAndAdvanceHours(LEARNING, 5)

		when:
		List<TimelineSegment> segments = createTimelineSegmentAndSplit()

		then:
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertLinkedTimeBand(segments[0].timeBandGroups[0].linkedTimeBands, 0, LEARNING, Duration.ofHours(2))
		validator.assertLinkedTimeBand(segments[0].timeBandGroups[0].linkedTimeBands, 1, REWORK, Duration.ofHours(3))
		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusHours(6))
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 0, REWORK, Duration.ofHours(4))
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 1, LEARNING, Duration.ofHours(5))
		validator.assertValidationComplete(segments, 2)
	}

	def "WHEN non subtask events are present SHOULD split across TimelineSegments"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.note()
		testSupport.advanceHours(1)
		testSupport.startSubtaskAndAdvanceHours(0)
		testSupport.note()
		testSupport.advanceHours(1)
		testSupport.note()

		when:
		List<TimelineSegment> segments = createTimelineSegmentAndSplit()

		then:
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segments[0].ideaFlowBands, 1, LEARNING, Duration.ofHours(2))
		validator.assertEvent(segments[0], 0, EventType.NOTE, start.plusHours(2))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, LEARNING, Duration.ofHours(1))
		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusHours(3))
		validator.assertEvent(segments[1], 1, EventType.NOTE, start.plusHours(3))
		validator.assertEvent(segments[1], 2, EventType.NOTE, start.plusHours(4))
		validator.assertValidationComplete(segments, 2)
	}

	def "WHEN split first segment SHOULD retain description of input segment AND subsequent segments should set description to subtask description"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.note()
		testSupport.startSubtaskAndAdvanceHours("subtask comment", 1)

		when:
		List<TimelineSegment> segments = createTimelineSegmentAndSplit()

		then:
		assert segments[0].description == inputSegment.description
		assert segments[1].description == "subtask comment"
	}

}

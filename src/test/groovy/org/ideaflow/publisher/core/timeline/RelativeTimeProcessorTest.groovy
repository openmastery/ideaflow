package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.TestTimelineSegmentBuilder
import org.ideaflow.publisher.api.timeline.Timeline
import org.ideaflow.publisher.api.timeline.TimelineSegment
import spock.lang.Specification

import java.time.Duration

import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.REWORK

class RelativeTimeProcessorTest extends Specification {

	TimelineTestSupport testSupport = new TimelineTestSupport()
	TestTimelineSegmentBuilder builder = new TestTimelineSegmentBuilder()

	def setup() {
		testSupport.startTaskAndAdvanceHours(1)
	}

	private Timeline processRelativeTime() {
		Timeline timeline = Timeline.builder()
				.timelineSegments([builder.build()])
				.build()
		new RelativeTimeProcessor().setRelativeTime(timeline)
		timeline
	}

	def "WHEN there is no idle"() {
		given:
		builder.ideaFlowBand(LEARNING, 0, 4)
				.nestedConflict(1, 3)
				.ideaFlowBand(PROGRESS, 5, 7)
				.linkedIdeaFlowBand(LEARNING, 7, 10)
				.linkedIdeaFlowBand(REWORK, 10, 12)

		when:
		Timeline timeline = processRelativeTime()

		then:
		TimelineSegment segment = timeline.timelineSegments[0]
		assert segment.ideaFlowBands[0].relativeStart == 0
		assert segment.ideaFlowBands[0].nestedBands[0].relativeStart == Duration.ofHours(1).seconds
		assert segment.ideaFlowBands[1].relativeStart == Duration.ofHours(5).seconds
		assert segment.timeBandGroups[0].relativeStart == Duration.ofHours(7).seconds
		assert segment.timeBandGroups[0].linkedTimeBands[0].relativeStart == Duration.ofHours(7).seconds
		assert segment.timeBandGroups[0].linkedTimeBands[1].relativeStart == Duration.ofHours(10).seconds
	}

	def "WHEN idle in prior nested conflict"() {
		given:
		builder.ideaFlowBand(LEARNING, 0, 6)
				.nestedConflict(2, 4)
				.idle(3, 4)
				.nestedConflict(5, 6)
				.ideaFlowBand(PROGRESS, 7, 8)

		when:
		Timeline timeline = processRelativeTime()

		then:
		TimelineSegment segment = timeline.timelineSegments[0]
		assert segment.ideaFlowBands[0].relativeStart == 0
		assert segment.ideaFlowBands[0].nestedBands[0].relativeStart == Duration.ofHours(2).seconds
		assert segment.ideaFlowBands[0].nestedBands[1].relativeStart == Duration.ofHours(4).seconds
		assert segment.ideaFlowBands[1].relativeStart == Duration.ofHours(6).seconds
	}

	def "WHEN segment has a mix of nested conflict and idle"() {
		given:
		builder.ideaFlowBand(LEARNING, 0, 10)
				.idle(1, 2)
				.nestedConflict(3, 4)
				.idle(4, 5)
				.idle(6, 7)
				.nestedConflict(7, 8)
				.idle(9, 10)
				.ideaFlowBand(PROGRESS, 10, 12)

		when:
		Timeline timeline = processRelativeTime()

		then:
		TimelineSegment segment = timeline.timelineSegments[0]
		assert segment.ideaFlowBands[0].relativeStart == 0
		assert segment.ideaFlowBands[0].nestedBands[0].relativeStart == Duration.ofHours(2).seconds
		assert segment.ideaFlowBands[0].nestedBands[1].relativeStart == Duration.ofHours(4).seconds
		assert segment.ideaFlowBands[1].relativeStart == Duration.ofHours(6).seconds
	}

	def "WHEN segment has idle within a TimeBandGroup"() {
		given:
		builder.linkedIdeaFlowBand(REWORK, 0, 4)
				.idle(0, 1)
				.idle(3, 4)
				.linkedIdeaFlowBand(LEARNING, 4, 7)
				.idle(5, 6)
				.ideaFlowBand(PROGRESS, 7, 8)

		when:
		Timeline timeline = processRelativeTime()

		then:
		TimelineSegment segment = timeline.timelineSegments[0]
		assert segment.timeBandGroups[0].relativeStart == 0
		assert segment.timeBandGroups[0].linkedTimeBands[0].relativeStart == 0
		assert segment.timeBandGroups[0].linkedTimeBands[1].relativeStart == Duration.ofHours(2).seconds
		assert segment.ideaFlowBands[0].relativeStart == Duration.ofHours(4).seconds
	}

	def "WHEN segment has idle within multiple TimeBandGroups"() {
		given:
		builder.linkedIdeaFlowBand(REWORK, 0, 4)
				.linkedIdeaFlowBand(LEARNING, 4, 7)
				.idle(5, 6)
				.ideaFlowBand(PROGRESS, 7, 8)
				.linkedIdeaFlowBand(LEARNING, 8, 12)
				.idle(9, 11)
				.linkedIdeaFlowBand(REWORK, 12, 13)

		when:
		Timeline timeline = processRelativeTime()

		then:
		TimelineSegment segment = timeline.timelineSegments[0]
		assert segment.timeBandGroups[0].relativeStart == 0
		assert segment.timeBandGroups[0].linkedTimeBands[0].relativeStart == 0
		assert segment.timeBandGroups[0].linkedTimeBands[1].relativeStart == Duration.ofHours(4).seconds
		assert segment.ideaFlowBands[0].relativeStart == Duration.ofHours(6).seconds
		assert segment.timeBandGroups[1].relativeStart == Duration.ofHours(7).seconds
		assert segment.timeBandGroups[1].linkedTimeBands[0].relativeStart == Duration.ofHours(7).seconds
		assert segment.timeBandGroups[1].linkedTimeBands[1].relativeStart == Duration.ofHours(9).seconds
	}

}

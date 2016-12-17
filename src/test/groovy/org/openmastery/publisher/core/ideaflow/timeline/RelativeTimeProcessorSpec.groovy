package org.openmastery.publisher.core.ideaflow.timeline

import org.openmastery.publisher.api.TestTimelineSegmentBuilder
import org.openmastery.publisher.core.Positionable
import org.openmastery.publisher.core.ideaflow.timeline.RelativeTimeProcessor
import org.openmastery.publisher.core.timeline.BandTimelineSegment
import org.openmastery.publisher.core.timeline.TimelineTestSupport
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.LEARNING
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.REWORK

class RelativeTimeProcessorSpec extends Specification {

	TimelineTestSupport testSupport = new TimelineTestSupport()
	TestTimelineSegmentBuilder builder = new TestTimelineSegmentBuilder()

	def setup() {
		testSupport.startTaskAndAdvanceHours(1)
	}

	private BandTimelineSegment processRelativeTime() {
		BandTimelineSegment segment = builder.build()
		List<Positionable> positionables = segment.getAllContentsFlattenedAsPositionableList()
		new RelativeTimeProcessor().computeRelativeTime(positionables)
		segment
	}

	def "WHEN there is no idle"() {
		given:
		builder.ideaFlowBand(LEARNING, 0, 4)
				.nestedConflict(1, 3)
				.ideaFlowBand(PROGRESS, 5, 7)
				.linkedIdeaFlowBand(LEARNING, 7, 10)
				.linkedIdeaFlowBand(REWORK, 10, 12)

		when:
		BandTimelineSegment segment = processRelativeTime()

		then:
		assert segment.ideaFlowBands[0].relativePositionInSeconds == 0l
		assert segment.ideaFlowBands[0].nestedBands[0].relativePositionInSeconds == Duration.ofHours(1).seconds
		assert segment.ideaFlowBands[1].relativePositionInSeconds == Duration.ofHours(5).seconds
		assert segment.timeBandGroups[0].relativePositionInSeconds == Duration.ofHours(7).seconds
		assert segment.timeBandGroups[0].linkedTimeBands[0].relativePositionInSeconds == Duration.ofHours(7).seconds
		assert segment.timeBandGroups[0].linkedTimeBands[1].relativePositionInSeconds == Duration.ofHours(10).seconds
	}

	def "WHEN idle in prior nested conflict"() {
		given:
		builder.ideaFlowBand(LEARNING, 0, 6)
				.nestedConflict(2, 4)
				.idle(3, 4)
				.nestedConflict(5, 6)
				.ideaFlowBand(PROGRESS, 7, 8)

		when:
		BandTimelineSegment segment = processRelativeTime()

		then:
		assert segment.ideaFlowBands[0].relativePositionInSeconds == 0l
		assert segment.ideaFlowBands[0].nestedBands[0].relativePositionInSeconds == Duration.ofHours(2).seconds
		assert segment.ideaFlowBands[0].nestedBands[1].relativePositionInSeconds == Duration.ofHours(4).seconds
		assert segment.ideaFlowBands[1].relativePositionInSeconds == Duration.ofHours(6).seconds
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
		BandTimelineSegment segment = processRelativeTime()

		then:
		assert segment.ideaFlowBands[0].relativePositionInSeconds == 0l
		assert segment.ideaFlowBands[0].nestedBands[0].relativePositionInSeconds == Duration.ofHours(2).seconds
		assert segment.ideaFlowBands[0].nestedBands[1].relativePositionInSeconds == Duration.ofHours(4).seconds
		assert segment.ideaFlowBands[1].relativePositionInSeconds == Duration.ofHours(6).seconds
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
		BandTimelineSegment segment = processRelativeTime()

		then:
		assert segment.timeBandGroups[0].relativePositionInSeconds == 0l
		assert segment.timeBandGroups[0].linkedTimeBands[0].relativePositionInSeconds == 0l
		assert segment.timeBandGroups[0].linkedTimeBands[1].relativePositionInSeconds == Duration.ofHours(2).seconds
		assert segment.ideaFlowBands[0].relativePositionInSeconds == Duration.ofHours(4).seconds
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
		BandTimelineSegment segment = processRelativeTime()

		then:
		assert segment.timeBandGroups[0].relativePositionInSeconds == 0l
		assert segment.timeBandGroups[0].linkedTimeBands[0].relativePositionInSeconds == 0l
		assert segment.timeBandGroups[0].linkedTimeBands[1].relativePositionInSeconds == Duration.ofHours(4).seconds
		assert segment.ideaFlowBands[0].relativePositionInSeconds == Duration.ofHours(6).seconds
		assert segment.timeBandGroups[1].relativePositionInSeconds == Duration.ofHours(7).seconds
		assert segment.timeBandGroups[1].linkedTimeBands[0].relativePositionInSeconds == Duration.ofHours(7).seconds
		assert segment.timeBandGroups[1].linkedTimeBands[1].relativePositionInSeconds == Duration.ofHours(9).seconds
	}

	def "TODO add event test"() {
		given:
		LocalDateTime now = LocalDateTime.now()
		boolean shouldFail = now.year != 2016

		expect:
		assert shouldFail == false
	}

}

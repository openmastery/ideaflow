package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.Timeline
import org.ideaflow.publisher.api.TimelineSegment
import spock.lang.Specification

import java.time.Duration

import static org.ideaflow.publisher.api.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.IdeaFlowStateType.PROGRESS

class TimelineGeneratorSpec extends Specification {

	TimelineSegmentValidator validator = new TimelineSegmentValidator()
	TimelineTestSupport testSupport = new TimelineTestSupport()

	def setup() {
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
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segments[0].ideaFlowBands, 1, LEARNING, Duration.ofHours(2), Duration.ofHours(3))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, LEARNING, Duration.ofHours(1), Duration.ofHours(2))
	}

}

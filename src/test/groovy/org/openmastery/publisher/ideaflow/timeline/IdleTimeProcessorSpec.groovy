package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.Duration
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.ideaflow.IdeaFlowStateEntity
import org.openmastery.publisher.core.timeline.BandTimelineSegment
import org.openmastery.publisher.core.timeline.BandTimelineSegmentBuilder
import org.openmastery.publisher.core.timeline.TimelineSegmentValidator
import org.openmastery.publisher.core.timeline.TimelineTestSupport
import spock.lang.Specification

import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.LEARNING
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.TROUBLESHOOTING

class IdleTimeProcessorSpec extends Specification {

	TimelineSegmentValidator validator = new TimelineSegmentValidator()
	TimelineTestSupport testSupport = new TimelineTestSupport()

	def setup() {
		testSupport.startTaskAndAdvanceHours(1)
	}

	private List<IdeaFlowBandModel> parseIdleTimeAndReturnIdeaFlowBandList() {
		List<IdeaFlowStateEntity> stateList = testSupport.getStateListWithActiveCompleted()

		BandTimelineSegment segment = new BandTimelineSegmentBuilder(stateList)
				.events(testSupport.getEventList())
				.build()

		IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
		idleTimeProcessor.collapseIdleTime(segment.ideaFlowBands, testSupport.getIdleActivityList())
		segment.ideaFlowBands
	}

	def "WHEN idle time is within a Timeband SHOULD subtract relative time from band"() {
		given:
		testSupport.startBandAndAdvanceHours(TROUBLESHOOTING, 1)
		testSupport.endBandAndAdvanceHours(TROUBLESHOOTING, 2)
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.idle(3)
		testSupport.advanceHours(1)

		when:
		List<IdeaFlowBandModel> ideaFlowBands = parseIdleTimeAndReturnIdeaFlowBandList()

		then:
		validator.assertTimeBand(ideaFlowBands, 0, PROGRESS, Duration.standardHours(1))
		validator.assertTimeBand(ideaFlowBands, 1, TROUBLESHOOTING, Duration.standardHours(1))
		validator.assertTimeBand(ideaFlowBands, 2, PROGRESS, Duration.standardHours(2))
		validator.assertTimeBand(ideaFlowBands, 3, LEARNING, Duration.standardHours(2), Duration.standardHours(3))
	}

	def "WHEN idle time is within a nested Timeband SHOULD subtract relative time from parent and child band"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.startBandAndAdvanceHours(TROUBLESHOOTING, 2)
		testSupport.idle(4)
		testSupport.advanceHours(1)

		when:
		List<IdeaFlowBandModel> ideaFlowBands = parseIdleTimeAndReturnIdeaFlowBandList()

		then:
		validator.assertTimeBand(ideaFlowBands, 0, PROGRESS, Duration.standardHours(1))
		validator.assertTimeBand(ideaFlowBands, 1, LEARNING, Duration.standardHours(4), Duration.standardHours(4))
		List nestedBands = ideaFlowBands[1].nestedBands
		validator.assertNestedTimeBand(nestedBands, 0, TROUBLESHOOTING, Duration.standardHours(3), Duration.standardHours(4))
	}

	def "WHEN multiple idles within band SHOULD provide total idle duration"() {
		given:
		testSupport.startBandAndAdvanceHours(LEARNING, 1)
		testSupport.idle(2)
		testSupport.advanceHours(3)
		testSupport.idle(4)

		when:
		List<IdeaFlowBandModel> ideaFlowBands = parseIdleTimeAndReturnIdeaFlowBandList()

		then:
		validator.assertTimeBand(ideaFlowBands, 0, PROGRESS, Duration.standardHours(1))
		validator.assertTimeBand(ideaFlowBands, 1, LEARNING, Duration.standardHours(4), Duration.standardHours(6))
	}

}

package org.openmastery.publisher.core.timeline

import org.joda.time.Duration
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.ideaflow.IdeaFlowStateEntity
import spock.lang.Specification

import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.TROUBLESHOOTING
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.LEARNING
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.REWORK

class BandTimelineSegmentFactorySpec extends Specification {

	TimelineSegmentValidator validator = new TimelineSegmentValidator()
	TimelineTestSupport testSupport = new TimelineTestSupport()

	def setup() {
		testSupport.startTaskAndAdvanceHours(1)
	}

	private BandTimelineSegment generatePrimaryTimeline() {
		List<IdeaFlowStateEntity> stateList = testSupport.getStateListWithActiveCompleted()
		new BandTimelineSegmentBuilder(stateList)
				.events(testSupport.getEventList())
				.build()
	}

	def "SHOULD calculate duration for all TimeBands"() {
		given:
		testSupport.startBandAndAdvanceHours(REWORK, 2)
		testSupport.endBandAndAdvanceHours(REWORK, 1)

		when:
		BandTimelineSegment segment = generatePrimaryTimeline()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.standardHours(1))
		validator.assertTimeBand(segment.ideaFlowBands, 1, REWORK, Duration.standardHours(2))
		validator.assertTimeBand(segment.ideaFlowBands, 2, PROGRESS, Duration.standardHours(1))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.standardHours(4)
	}

	def "WHEN IdeaFlowStates are nested SHOULD create nested TimeBands"() {
		given:
		testSupport.startBandAndAdvanceHours(REWORK, 1)
		testSupport.startBandAndAdvanceHours(TROUBLESHOOTING, 2)
		testSupport.endBandAndAdvanceHours(TROUBLESHOOTING, 1)
		testSupport.startBandAndAdvanceHours(TROUBLESHOOTING, 3)
		testSupport.endBandAndAdvanceHours(TROUBLESHOOTING, 1)

		when:
		BandTimelineSegment segment = generatePrimaryTimeline()


		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.standardHours(1))
		validator.assertTimeBand(segment.ideaFlowBands, 1, REWORK, Duration.standardHours(8))
		List<IdeaFlowBandModel> nestedBands = segment.ideaFlowBands[1].nestedBands
		validator.assertNestedTimeBand(nestedBands, 0, TROUBLESHOOTING, Duration.standardHours(2))
		validator.assertNestedTimeBand(nestedBands, 1, TROUBLESHOOTING, Duration.standardHours(3))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.standardHours(9)
	}

	def "WHEN IdeaFlowStates are linked SHOULD group bands into a TimeBandGroup"() {
		given:
		testSupport.startBandAndAdvanceHours(TROUBLESHOOTING, 1)
		testSupport.startBandAndAdvanceHours(LEARNING, 3)
		testSupport.startBandAndAdvanceHours(REWORK, 2)

		when:
		BandTimelineSegment segment = generatePrimaryTimeline()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.standardHours(1))
		List groupedIdeaFlowBands = segment.timeBandGroups[0].linkedTimeBands
		validator.assertLinkedTimeBand(groupedIdeaFlowBands, 0, TROUBLESHOOTING, Duration.standardHours(1))
		validator.assertLinkedTimeBand(groupedIdeaFlowBands, 1, LEARNING, Duration.standardHours(3))
		validator.assertLinkedTimeBand(groupedIdeaFlowBands, 2, REWORK, Duration.standardHours(2))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.standardHours(7)
	}

	def "WHEN IdeaFlowStates are linked AND first state has nested conflicts SHOULD create TimeBandGroup including all bands"() {
		given:
		testSupport.startBandAndAdvanceHours(REWORK, 2)
		testSupport.startBandAndAdvanceHours(TROUBLESHOOTING, 1)
		testSupport.endBandAndAdvanceHours(TROUBLESHOOTING, 3)
		testSupport.startBandAndAdvanceHours(LEARNING, 4)

		when:
		BandTimelineSegment segment = generatePrimaryTimeline()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.standardHours(1))
		List linkedIdeaFlowBands = segment.timeBandGroups[0].linkedTimeBands
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 0, REWORK, Duration.standardHours(6))
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 1, LEARNING, Duration.standardHours(4))
		List nestedIdeaFlowBands = segment.timeBandGroups[0].linkedTimeBands[0].nestedBands
		validator.assertNestedTimeBand(nestedIdeaFlowBands, 0, TROUBLESHOOTING, Duration.standardHours(1))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.standardHours(11)
	}

	def "WHEN conflict is unnested SHOULD be considered linked AND previous duration should be reduced by the band overlap"() {
		given:

		//conflict <- learning <-rework <- unnested conflict (rework ends after conflict start) <- learning
		testSupport.startBandAndAdvanceHours(TROUBLESHOOTING, 1)
		testSupport.startBandAndAdvanceHours(LEARNING, 2)
		testSupport.startBandAndAdvanceHours(REWORK, 1)
		testSupport.startBandAndAdvanceHours(TROUBLESHOOTING, 3) //nested
		testSupport.endBandAndAdvanceHours(REWORK, 4) //unnest the conflict so it's linkable
		testSupport.startBandAndAdvanceHours(LEARNING, 5)
		testSupport.endBandAndAdvanceHours(LEARNING, 2) //finish the group

		when:
		BandTimelineSegment segment = generatePrimaryTimeline()

		then:
		validator.assertTimeBand(segment.ideaFlowBands, 0, PROGRESS, Duration.standardHours(1))
		List linkedIdeaFlowBands = segment.timeBandGroups[0].linkedTimeBands
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 0, TROUBLESHOOTING, Duration.standardHours(1))
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 1, LEARNING, Duration.standardHours(2))
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 2, REWORK, Duration.standardHours(1))
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 3, TROUBLESHOOTING, Duration.standardHours(7))
		validator.assertLinkedTimeBand(linkedIdeaFlowBands, 4, LEARNING, Duration.standardHours(5))
		assert segment.duration == Duration.standardHours(19)
	}

}

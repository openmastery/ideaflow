package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.Duration
import org.joda.time.LocalDateTime
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.ideaflow.IdeaFlowStateEntity
import org.openmastery.publisher.core.timeline.BandTimelineSegment
import org.openmastery.publisher.core.timeline.BandTimelineSegmentBuilder
import org.openmastery.publisher.core.timeline.TimelineSegmentValidator
import org.openmastery.publisher.core.timeline.TimelineTestSupport
import org.openmastery.time.MockTimeService
import spock.lang.Specification

import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.LEARNING
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.TROUBLESHOOTING

class IdleTimeProcessorSpec extends Specification {

	MockTimeService timeService = new MockTimeService()
	TimelineSegmentValidator validator = new TimelineSegmentValidator()
	TimelineTestSupport testSupport = new TimelineTestSupport(timeService)
	IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(timeService)
	IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()

	def setup() {
		testSupport.startTaskAndAdvanceHours(1)
	}

	private List<IdeaFlowBandModel> parseIdleTimeAndReturnIdeaFlowBandList() {
		List<IdeaFlowStateEntity> stateList = testSupport.getStateListWithActiveCompleted()

		BandTimelineSegment segment = new BandTimelineSegmentBuilder(stateList)
				.events(testSupport.getEventList())
				.build()

		EntityMapper entityMapper = new EntityMapper()
		List<IdleTimeBandModel> idleTimeBandModelList = entityMapper.mapList(testSupport.getIdleActivityList(), IdleTimeBandModel)
		idleTimeProcessor.collapseIdleTime(segment.ideaFlowBands, idleTimeBandModelList)
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

	def "generateIdleTimeBandsFromDeativationEvents should generate idle for deactivation/activation pair"() {
		given:
		LocalDateTime idleStartTime = timeService.now()
		builder.deactivate()
				.advanceHours(2)
				.activate()

		when:
		List<IdleTimeBandModel> idleBands = idleTimeProcessor.generateIdleTimeBandsFromDeativationEvents(builder.eventList)

		then:
		assert idleBands[0].duration == Duration.standardHours(2)
		assert idleBands[0].start == idleStartTime
		assert idleBands.size() == 1
	}

	def "generateIdleTimeBandsFromDeativationEvents should ignore spurious deactivation and activation events"() {
		given:
		LocalDateTime idleStartTime = timeService.now()
		builder.deactivate()
				.advanceHours(1)
				.deactivate()
				.advanceHours(2)
				.activate()
				.advanceHours(1)
				.activate()

		when:
		List<IdleTimeBandModel> idleBands = idleTimeProcessor.generateIdleTimeBandsFromDeativationEvents(builder.eventList)

		then:
		assert idleBands[0].duration == Duration.standardHours(3)
		assert idleBands[0].start == idleStartTime
		assert idleBands.size() == 1
	}

}

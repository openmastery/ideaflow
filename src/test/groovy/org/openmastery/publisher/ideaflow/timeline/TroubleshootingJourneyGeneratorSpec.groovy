package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.time.MockTimeService
import spock.lang.Specification

class TroubleshootingJourneyGeneratorSpec extends Specification {


	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	private TroubleshootingJourneyGenerator journeyGenerator = new TroubleshootingJourneyGenerator()

	LocalDateTime start

	def setup() {
		start = mockTimeService.now()
	}



	def "createJourney SHOULD break up WTFs within band ranges"() {
		given:
		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(15 * 60)
				.build()

		builder.activate()
		builder.advanceMinutes(5)
		builder.wtf()
		builder.advanceMinutes(1)
		builder.wtf()
		builder.advanceMinutes(4)
		builder.wtf()

		when:
		List<Event> wtfYayEvents =  builder.eventList.findAll {it.type == EventType.WTF}
		TroubleshootingJourney journey = journeyGenerator.createJourney(wtfYayEvents, troubleshootingBand)

		then:
		assert journey.band == troubleshootingBand
		assert journey.experiments != null
		assert journey.experiments.size() == 3
	}

	def "splitIntoJourneys SHOULD break up WTFs across bands"() {
		given:
		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(15 * 60)
				.build()
		IdeaFlowBand troubleshootingBand2 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(30 * 60)
				.durationInSeconds(40 * 60)
				.build()


		builder.activate()
		builder.advanceMinutes(5)
		builder.wtf()
		builder.advanceMinutes(45)
		builder.wtf()
		builder.advanceMinutes(4)
		builder.wtf()


		when:
		List<Event> wtfYayEvents =  builder.eventList.findAll {it.type == EventType.WTF}
		List<TroubleshootingJourney> journeys = journeyGenerator.splitIntoJourneys(wtfYayEvents, [troubleshootingBand, troubleshootingBand2])

		then:
		assert journeys != null
		assert journeys.size() == 2

		assert journeys.get(0).band == troubleshootingBand
		assert journeys.get(0).experiments != null
		assert journeys.get(0).experiments.size() == 1

		assert journeys.get(1).band == troubleshootingBand2
		assert journeys.get(1).experiments != null
		assert journeys.get(1).experiments.size() == 2

	}



	def "createJourney SHOULD break up execution activity across experiments"() {
		given:
		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(15 * 60)
				.build()

		builder.activate()
		builder.advanceMinutes(5)
		builder.wtf() //experiment 1
		builder.advanceMinutes(1)
		builder.executeCode()
		builder.advanceMinutes(1)
		builder.wtf() //experiment 2
		builder.advanceMinutes(1)
		builder.executeCode()
		builder.advanceMinutes(1)
		builder.executeCode()

		when:
		TroubleshootingJourney journey = journeyGenerator.createJourney(builder.eventList, troubleshootingBand, builder.executionEventList)
		then:

		assert journey.band == troubleshootingBand
		assert journey.experiments != null
		assert journey.experiments.size() == 2
		assert journey.experiments.get(0).executionCycles.size() == 1
		assert journey.experiments.get(1).executionCycles.size() == 2

	}

	def "createJourney SHOULD divide up band duration by experiment cycles"() {
		given:
		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(60)
				.durationInSeconds(30 * 60)
				.build()

		builder.activate()
		builder.advanceMinutes(1)
		builder.wtf() //experiment start
		builder.advanceMinutes(1)
		builder.executeCode() //cycle 1
		builder.advanceMinutes(2)
		builder.executeCode() //cycle 2
		builder.advanceMinutes(5)
		builder.executeCode() //cycle 3
		builder.advanceMinutes(3)
		builder.executeCode() //cycle 4 (to end)

		when:
		TroubleshootingJourney journey = journeyGenerator.createJourney(builder.eventList, troubleshootingBand, builder.executionEventList)
		then:

		assert journey.band == troubleshootingBand

		assert journey.experiments.get(0).executionCycles.size() == 4
		assert journey.experiments.get(0).executionCycles.get(0).durationInSeconds == (2 * 60L)
		assert journey.experiments.get(0).executionCycles.get(1).durationInSeconds == (5 * 60L)
		assert journey.experiments.get(0).executionCycles.get(2).durationInSeconds == (3 * 60L)
		assert journey.experiments.get(0).executionCycles.get(3).durationInSeconds == (19 * 60L)

	}

	def "createJourney SHOULD find all tags in comments and set them on tags"() {
		given:
		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(15 * 60)
				.build()

		builder.activate()
		builder.advanceMinutes(5)
		builder.wtf("This comment has a #hashtag")

		Set expectedTags = ['#hashtag']

		when:
		TroubleshootingJourney journey = journeyGenerator.createJourney(builder.eventList, troubleshootingBand)

		then:
		assert journey.band == troubleshootingBand
		assert journey.tags == expectedTags

		assert journey.experiments != null
		assert journey.experiments.size() == 1
		assert journey.experiments.first().tags == expectedTags
	}



}

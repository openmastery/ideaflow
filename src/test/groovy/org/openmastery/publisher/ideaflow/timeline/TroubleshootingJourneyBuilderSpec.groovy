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

class TroubleshootingJourneyBuilderSpec extends Specification {


	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	private TroubleshootingJourneyBuilder journeyBuilder = new TroubleshootingJourneyBuilder()

	LocalDateTime start

	def setup() {
		start = mockTimeService.now()
	}



	def "splitIntoJourneys SHOULD break up WTFs within band ranges"() {
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
		List<TroubleshootingJourney> journeys = journeyBuilder.splitIntoJourneys(wtfYayEvents, [troubleshootingBand])

		then:
		assert journeys != null
		assert journeys.size() == 1
		assert journeys.get(0).band == troubleshootingBand
		assert journeys.get(0).experiments != null
		assert journeys.get(0).experiments.size() == 3
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
		List<TroubleshootingJourney> journeys = journeyBuilder.splitIntoJourneys(wtfYayEvents, [troubleshootingBand, troubleshootingBand2])

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



	def "fillJourneyWithData SHOULD break up execution activity across experiments"() {
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
		TroubleshootingJourney journey = createJourney(builder.eventList, troubleshootingBand)
		IdeaFlowTimeline timeline = new IdeaFlowTimeline(events: builder.eventList, executionEvents: builder.executionEventList)
		journeyBuilder.fillJourneyWithActivity(journey, timeline)

		then:

		assert journey.band == troubleshootingBand
		assert journey.experiments != null
		assert journey.experiments.size() == 2
		assert journey.experiments.get(0).executionCycles.size() == 1
		assert journey.experiments.get(1).executionCycles.size() == 2

	}




	def "fillJourneyWithData SHOULD divide up band duration by experiment cycles"() {
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
		TroubleshootingJourney journey = createJourney(builder.eventList, troubleshootingBand)
		IdeaFlowTimeline timeline = new IdeaFlowTimeline(events: builder.eventList, executionEvents: builder.executionEventList)
		journeyBuilder.fillJourneyWithActivity(journey, timeline)

		then:

		assert journey.band == troubleshootingBand
		assert journey.experiments != null
		assert journey.experiments.size() == 1
		assert journey.experiments.get(0).executionCycles.size() == 4
		assert journey.experiments.get(0).executionCycles.get(0).durationInSeconds == (2 * 60L)
		assert journey.experiments.get(0).executionCycles.get(1).durationInSeconds == (5 * 60L)
		assert journey.experiments.get(0).executionCycles.get(2).durationInSeconds == (3 * 60L)
		assert journey.experiments.get(0).executionCycles.get(3).durationInSeconds == (19 * 60L)


	}


	TroubleshootingJourney createJourney(List<Event> events, IdeaFlowBand band) {
		List<Event> wtfYayEvents =  events.findAll {it.type == EventType.WTF}
		return journeyBuilder.splitIntoJourneys(wtfYayEvents, [band]).get(0)
	}
}

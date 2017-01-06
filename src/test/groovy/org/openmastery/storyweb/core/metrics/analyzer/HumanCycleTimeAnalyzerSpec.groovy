package org.openmastery.storyweb.core.metrics.analyzer

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.storyweb.api.metrics.GraphPoint
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.publisher.ideaflow.story.TroubleshootingJourneyGenerator
import org.openmastery.time.MockTimeService
import spock.lang.Specification


class HumanCycleTimeAnalyzerSpec extends Specification {
	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)
	private TroubleshootingJourneyGenerator journeyGenerator = new TroubleshootingJourneyGenerator()

	private HumanCycleTimeAnalyzer calculator = new HumanCycleTimeAnalyzer()


	LocalDateTime start

	def setup() {
		start = mockTimeService.now()
	}

	def "analyzeTimelineAndJourneys SHOULD return the ratio of (total time / numberEvents) "() {
		given:
		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(5 * 60)
				.build()

		builder.activate()
		builder.wtf()
		builder.advanceMinutes(1)
		builder.executeCode()
		builder.advanceMinutes(2)
		builder.executeCode()
		builder.advanceMinutes(1)
		builder.executeCode()
		builder.advanceMinutes(1)

		TroubleshootingJourney journey = journeyGenerator.createJourney(builder.eventList, troubleshootingBand, builder.executionEventList)
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [troubleshootingBand], executionEvents: builder.executionEventList)

		when:
		GraphPoint<DurationInSeconds> timelinePoint = calculator.analyzeIdeaFlowStory(timeline, [journey])

		then:

		assert timelinePoint.childPoints.get(0).relativePath == "/journey/1"
		assert timelinePoint.childPoints.get(0).value == new DurationInSeconds((Long)(4 * 60)/ 3)
		assert timelinePoint.childPoints.get(0).metricType == MetricType.AVG_HUMAN_CYCLE_RATIOS

	}

	def "analyzeTimelineAndJourneys SHOULD return the avg of ratios WHEN there's multiple bands"() {
		given:
		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(10 * 60)
				.build()

		IdeaFlowBand troubleshootingBand2 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(11 * 60)
				.durationInSeconds(4 * 60)
				.build()

		builder.activate()
		builder.wtf()
		builder.advanceMinutes(5)
		builder.executeCode()
		builder.advanceMinutes(5)
		builder.awesome()
		builder.advanceMinutes(1)
		builder.wtf()
		builder.executeCode()
		builder.advanceMinutes(1)
		builder.executeCode()
		builder.advanceMinutes(2)
		builder.executeCode()
		builder.advanceMinutes(1)
		builder.awesome()


		TroubleshootingJourney journey = journeyGenerator.createJourney(builder.eventList, troubleshootingBand, builder.executionEventList)
		TroubleshootingJourney journey2 = journeyGenerator.createJourney(builder.eventList, troubleshootingBand2, builder.executionEventList)

		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [troubleshootingBand, troubleshootingBand2], executionEvents: builder.executionEventList)

		when:
		GraphPoint<DurationInSeconds> timelinePoint = calculator.analyzeIdeaFlowStory(timeline, [journey, journey2])

		then:

		assert timelinePoint.childPoints.get(0).relativePath == "/journey/1"
		assert timelinePoint.childPoints.get(0).value == new DurationInSeconds((Long)(5*60))
		assert timelinePoint.childPoints.get(0).metricType == MetricType.AVG_HUMAN_CYCLE_RATIOS

		assert timelinePoint.childPoints.get(1).relativePath == "/journey/3"
		assert timelinePoint.childPoints.get(1).value == new DurationInSeconds((Long)((120+60+60)/3))

		assert timelinePoint.relativePath == "/timeline"
		assert timelinePoint.value == new DurationInSeconds((Long)((120+60+60+300)/4))
	}
}

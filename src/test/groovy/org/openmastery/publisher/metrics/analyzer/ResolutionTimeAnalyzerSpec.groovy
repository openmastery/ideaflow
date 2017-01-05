package org.openmastery.publisher.metrics.analyzer

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.metrics.GraphPoint
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.publisher.ideaflow.timeline.TroubleshootingJourneyGenerator
import org.openmastery.publisher.metrics.calculator.MaxResolutionTimeCalculator
import org.openmastery.time.MockTimeService
import spock.lang.Specification


class ResolutionTimeAnalyzerSpec extends Specification {
	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)
	private TroubleshootingJourneyGenerator journeyGenerator = new TroubleshootingJourneyGenerator()

	private ResolutionTimeAnalyzer calculator = new ResolutionTimeAnalyzer()

	LocalDateTime start

	def setup() {
		start = mockTimeService.now()
	}

	def "calculateMetrics SHOULD return the maximum amount of troubleshooting per incident"() {
		given:
		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(30 * 60)
				.build()

		IdeaFlowBand troubleshootingBand2 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(40 * 60)
				.durationInSeconds(60 * 60)
				.build()

		builder.activate()
		builder.wtf()
		builder.advanceMinutes(15)
		builder.executeCode()
		builder.advanceMinutes(15)
		builder.awesome()
		builder.advanceMinutes(10)
		builder.wtf()
		builder.executeCode()
		builder.advanceMinutes(20)
		builder.executeCode()
		builder.advanceMinutes(20)
		builder.executeCode()
		builder.advanceMinutes(20)
		builder.awesome()



		TroubleshootingJourney journey = journeyGenerator.createJourney(builder.eventList, troubleshootingBand, builder.executionEventList)
		TroubleshootingJourney journey2 = journeyGenerator.createJourney(builder.eventList, troubleshootingBand2, builder.executionEventList)

		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [troubleshootingBand, troubleshootingBand2], executionEvents: builder.executionEventList)

		when:
		GraphPoint<DurationInSeconds> timelinePoint = calculator.analyzeTimelineAndJourneys(timeline, [journey, journey2])

		then:

		assert timelinePoint.relativePath == "/timeline"
		assert timelinePoint.value == new DurationInSeconds((Long)(60 * 60))  //use max for aggregation

		assert timelinePoint.childPoints.get(0).relativePath == "/journey/1"
		assert timelinePoint.childPoints.get(0).value == new DurationInSeconds((Long)(30 * 60))

		assert timelinePoint.childPoints.get(1).relativePath == "/journey/3"
		assert timelinePoint.childPoints.get(1).value == new DurationInSeconds((Long)(60 * 60))



	}
}

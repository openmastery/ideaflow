package org.openmastery.publisher.metrics.subtask

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.publisher.ideaflow.timeline.JourneyTimeline
import org.openmastery.publisher.ideaflow.timeline.TroubleshootingJourneyGenerator
import org.openmastery.time.MockTimeService
import spock.lang.Specification

class MetricServiceSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	private TroubleshootingJourneyGenerator journeyGenerator = new TroubleshootingJourneyGenerator()
	private MetricsService metricsService = new MetricsService()

	LocalDateTime start

	def setup() {
		start = mockTimeService.now()
	}


	def "generateJourneyMetrics SHOULD calculate metrics even though limited amount of data"() {
		given:
		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(12 * 60)
				.build()

		builder.activate()
		builder.wtf()
		builder.advanceMinutes(5)
		builder.executeCode()
		builder.advanceMinutes(1)
		builder.executeCode()
		builder.advanceMinutes(4)
		builder.executeCode()
		builder.advanceMinutes(2)
		builder.awesome()

		when:
		TroubleshootingJourney journey = journeyGenerator.createJourney(builder.eventList, troubleshootingBand, builder.executionEventList)
		List<Metric> metrics = metricsService.generateJourneyMetrics(new JourneyTimeline(journey))

		then:
		assert metrics != null
		assert metrics.size() == 2

		Metric avgCyclesMetric = metrics.find() { Metric metric -> metric.type == MetricType.MAX_EXPERIMENT_CYCLE_COUNT }
		assert avgCyclesMetric.value == 3

		Metric avgCycleDurationMetric = metrics.find() { Metric metric -> metric.type == MetricType.MAX_HUMAN_CYCLE_RATIO }
		assert avgCycleDurationMetric.value == new DurationInSeconds((long)12/3 * 60)


	}

}

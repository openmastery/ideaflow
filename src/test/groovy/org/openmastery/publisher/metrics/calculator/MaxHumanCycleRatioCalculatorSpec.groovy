package org.openmastery.publisher.metrics.calculator

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.storyweb.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.time.MockTimeService
import spock.lang.Specification


class MaxHumanCycleRatioCalculatorSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	private MaxHumanCycleRatioCalculator calculator = new MaxHumanCycleRatioCalculator()


	LocalDateTime start

	def setup() {
		start = mockTimeService.now()
	}

	def "calculateMetrics SHOULD return the ratio of (total time / numberEvents) "() {
		given:
		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(15 * 60)
				.build()

		builder.activate()
		builder.advanceMinutes(1)
		builder.executeCode()
		builder.advanceMinutes(1)
		builder.executeCode()
		builder.advanceMinutes(1)
		builder.executeCode()

		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [troubleshootingBand], executionEvents: builder.executionEventList)
		Metric<DurationInSeconds> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.AVG_HUMAN_CYCLE_RATIOS
		assert metric.value == new DurationInSeconds( (Long)(15 * 60)/ 3)
	}

	def "calculateMetrics SHOULD return the max of ratios WHEN there's multiple bands"() {
		given:
		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(15 * 60)
				.build()

		IdeaFlowBand troubleshootingBand2 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(15 * 60)
				.durationInSeconds(20 * 60)
				.build()

		builder.activate()
		builder.advanceMinutes(5)
		builder.executeCode()
		builder.advanceMinutes(15) //5 min into the second band
		builder.executeCode()
		builder.advanceMinutes(1)
		builder.executeCode()
		builder.advanceMinutes(1)
		builder.executeCode()

		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [troubleshootingBand, troubleshootingBand2], executionEvents: builder.executionEventList)
		Metric<DurationInSeconds> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.AVG_HUMAN_CYCLE_RATIOS
		assert metric.value == new DurationInSeconds( (Long) 15 * 60 )
	}
}

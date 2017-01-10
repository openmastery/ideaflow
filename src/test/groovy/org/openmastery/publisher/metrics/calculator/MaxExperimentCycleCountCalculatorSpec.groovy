package org.openmastery.publisher.metrics.calculator

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.storyweb.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.time.MockTimeService
import spock.lang.Specification

class MaxExperimentCycleCountCalculatorSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	private MaxExperimentCycleCountCalculator calculator = new MaxExperimentCycleCountCalculator()


	LocalDateTime start

	def setup() {
		start = mockTimeService.now()
	}

	def "calculateMetrics SHOULD return the number of execution events WHEN there's only one band"() {
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
		Metric<Double> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.MAX_EXPERIMENT_CYCLES
		assert metric.value == 3D
	}

	def "calculateMetrics SHOULD return the max of execution events WHEN there's multiple bands"() {
		given:
		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(15 * 60)
				.build()

		IdeaFlowBand troubleshootingBand2 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(15 * 60)
				.durationInSeconds(15 * 60)
				.build()

		builder.activate()
		builder.advanceMinutes(5)
		builder.executeCode()
		builder.advanceMinutes(15) //5 min into the second band
		builder.executeCode()
		builder.advanceMinutes(1)
		builder.executeCode()

		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [troubleshootingBand, troubleshootingBand2], executionEvents: builder.executionEventList)
		Metric<Double> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.MAX_EXPERIMENT_CYCLES
		assert metric.value == 2.0D
	}
}

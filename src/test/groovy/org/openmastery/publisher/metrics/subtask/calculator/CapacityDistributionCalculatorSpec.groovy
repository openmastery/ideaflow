package org.openmastery.publisher.metrics.subtask.calculator

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.CapacityDistribution
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.time.MockTimeService
import spock.lang.Specification


class CapacityDistributionCalculatorSpec extends Specification {


	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	private CapacityDistributionCalculator calculator = new CapacityDistributionCalculator()


	LocalDateTime start

	def setup() {
		start = mockTimeService.now()
	}


	def "calculateMetrics SHOULD add up the duration for multiple bands of same type"() {
		given:
		IdeaFlowBand troubleshootingBand1 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(15)
				.build()

		IdeaFlowBand troubleshootingBand2 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(30)
				.build()

		when:
		IdeaFlowTimeline timeline = new IdeaFlowTimeline(ideaFlowBands: [troubleshootingBand1, troubleshootingBand2])
		Metric<CapacityDistribution> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.CAPACITY_DISTRIBUTION
		assert metric.value != null
		assert metric.value.timeSpentPerStateType.get(IdeaFlowStateType.TROUBLESHOOTING, 45)
	}

	def "calculateMetrics SHOULD add up the duration for bands by type"() {
		given:
		IdeaFlowBand learningBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.LEARNING)
				.relativePositionInSeconds(0)
				.durationInSeconds(30)
				.build()

		IdeaFlowBand progressBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.PROGRESS)
				.relativePositionInSeconds(0)
				.durationInSeconds(40)
				.build()

		IdeaFlowBand troubleshootingBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(15)
				.build()

		when:
		IdeaFlowTimeline timeline = new IdeaFlowTimeline(ideaFlowBands: [learningBand, progressBand, troubleshootingBand])
		Metric<CapacityDistribution> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.CAPACITY_DISTRIBUTION
		assert metric.value != null
		assert metric.value.timeSpentPerStateType.get(IdeaFlowStateType.LEARNING, 30)
		assert metric.value.timeSpentPerStateType.get(IdeaFlowStateType.PROGRESS, 40)
		assert metric.value.timeSpentPerStateType.get(IdeaFlowStateType.TROUBLESHOOTING, 15)
	}

}

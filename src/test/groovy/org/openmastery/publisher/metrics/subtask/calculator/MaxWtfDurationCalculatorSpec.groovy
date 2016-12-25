package org.openmastery.publisher.metrics.subtask.calculator

import org.joda.time.Duration
import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowMetricsTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.time.MockTimeService
import spock.lang.Specification


class MaxWtfDurationCalculatorSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	private MaxWtfDurationCalculator calculator = new MaxWtfDurationCalculator()


	LocalDateTime start

	def setup() {
		start = mockTimeService.now()
	}

	def "calculateMetrics SHOULD return the largest troubleshooting band"() {
		given:
		IdeaFlowBand troubleshooting1 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(30)
				.build()

		IdeaFlowBand troubleshooting2 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(0)
				.durationInSeconds(60)
				.build()

		when:
		IdeaFlowTimeline timeline = new IdeaFlowTimeline(ideaFlowBands: [troubleshooting1, troubleshooting2])
		Metric<Duration> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.MAX_WTF_DURATION
		assert metric.value == Duration.standardSeconds(60)
	}
}

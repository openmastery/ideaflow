package org.openmastery.publisher.metrics.calculator

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.time.MockTimeService
import spock.lang.Specification

class MaxWtfsPerDayCalculatorSpec extends Specification {


	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	private MaxWtfsPerDayCalculator calculator = new MaxWtfsPerDayCalculator()

	LocalDateTime start

	def setup() {
		start = mockTimeService.now()
	}


	def "calculateMetrics SHOULD count the WTFs in a single day"() {

		given:
		builder.activate()
				.advanceMinutes(15)
				.wtf()
				.advanceMinutes(15)
				.wtf()
				.advanceMinutes(15)
				.awesome()
				.deactivate()

		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(start: start, events: builder.eventList, end: mockTimeService.now())
		Metric<Double> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.WTFS_PER_DAY
		assert metric.value == 2.0D
		assert metric.valueType == "Double"

	}

	def "calculateMetrics SHOULD get the max number of WTFs across multiple days"() {
		given:
		builder.activate()
		.wtf()
		.advanceDays(1)
		.wtf()
		.wtf()
		.deactivate()

		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(start: start, events: builder.eventList, end: mockTimeService.now())
		Metric<Double> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.WTFS_PER_DAY
		assert metric.value == 2.0D
	}
}

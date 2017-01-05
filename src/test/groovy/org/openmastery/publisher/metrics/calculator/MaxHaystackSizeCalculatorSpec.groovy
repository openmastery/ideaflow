package org.openmastery.publisher.metrics.calculator

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.time.MockTimeService
import spock.lang.Specification


class MaxHaystackSizeCalculatorSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	private MaxHaystackSizeCalculator calculator = new MaxHaystackSizeCalculator()

	LocalDateTime start

	def setup() {
		start = mockTimeService.now()
	}

	def "calculateMetrics SHOULD identify the biggest consecutive time without execution within a band"() {
		given:
		IdeaFlowBand progressBand = IdeaFlowBand.builder()
										.type(IdeaFlowStateType.PROGRESS)
										.relativePositionInSeconds(0)
										.durationInSeconds(30)
										.build()
		ExecutionEvent event = new ExecutionEvent(relativePositionInSeconds: 2)

		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [progressBand], executionEvents: [event])
		Metric<DurationInSeconds> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.MAX_HAYSTACK_SIZE
		assert metric.value == new DurationInSeconds(28)
	}

	def "calculateMetrics SHOULD return entire interval when no execution events"() {
		given:
		IdeaFlowBand progressBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.PROGRESS)
				.relativePositionInSeconds(0)
				.durationInSeconds(30)
				.build()

		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [progressBand], executionEvents: [])
		Metric<DurationInSeconds> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.MAX_HAYSTACK_SIZE
		assert metric.value == new DurationInSeconds(30)
	}


	def "calculateMetrics SHOULD identify the biggest consecutive time across consecutive bands"() {
		given:
		IdeaFlowBand progressBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.PROGRESS)
				.relativePositionInSeconds(0)
				.durationInSeconds(30)
				.build()

		IdeaFlowBand consecutiveBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(30)
				.durationInSeconds(60)
				.build()

		ExecutionEvent event = new ExecutionEvent(relativePositionInSeconds: 2)

		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [progressBand, consecutiveBand], executionEvents: [event])
		Metric<DurationInSeconds> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.MAX_HAYSTACK_SIZE
		assert metric.value == new DurationInSeconds(88)
	}

	def "calculateMetrics SHOULD restart the count when there are time gaps"() {
		given:
		IdeaFlowBand progressBand1 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.PROGRESS)
				.relativePositionInSeconds(0)
				.durationInSeconds(30)
				.build()

		IdeaFlowBand progressBand2 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.PROGRESS)
				.relativePositionInSeconds(60)
				.durationInSeconds(15)
				.build()

		ExecutionEvent event = new ExecutionEvent(relativePositionInSeconds: 2)

		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [progressBand1, progressBand2], executionEvents: [event])
		Metric<DurationInSeconds> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.MAX_HAYSTACK_SIZE
		assert metric.value == new DurationInSeconds(28)
	}

	def "calculateMetrics SHOULD ignore learning bands"() {
		given:
		IdeaFlowBand learningBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.LEARNING)
				.relativePositionInSeconds(0)
				.durationInSeconds(30)
				.build()

		IdeaFlowBand progressBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.PROGRESS)
				.relativePositionInSeconds(30)
				.durationInSeconds(60)
				.build()

		ExecutionEvent eventInLearning = new ExecutionEvent(relativePositionInSeconds: 2)
		ExecutionEvent eventInProgress = new ExecutionEvent(relativePositionInSeconds: 35)


		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [learningBand, progressBand], executionEvents: [eventInLearning, eventInProgress])
		Metric<DurationInSeconds> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.MAX_HAYSTACK_SIZE
		assert metric.value == new DurationInSeconds(55)
	}


}

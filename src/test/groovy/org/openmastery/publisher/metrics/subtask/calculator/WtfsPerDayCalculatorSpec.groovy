package org.openmastery.publisher.metrics.subtask.calculator

import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineBuilder
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.time.MockTimeService
import spock.lang.Specification


class WtfsPerDayCalculatorSpec extends Specification {


	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	private WtfsPerDayCalculator calculator = new WtfsPerDayCalculator()

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
		IdeaFlowTimeline timeline = new IdeaFlowTimeline(events: builder.eventList)
		Metric<Double> metric = calculator.calculateMetrics(timeline)

		then:
		assert metric.type == MetricType.WTFS_PER_DAY
		assert metric.value == 2.0D

	}
}

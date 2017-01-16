package org.openmastery.storyweb.core.metrics.analyzer

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimelineBuilder
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.metrics.GraphPoint
import org.openmastery.time.MockTimeService
import spock.lang.Specification

class WtfsPerDayAnalyzerSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineBuilder builder = new IdeaFlowTimelineBuilder(mockTimeService)

	private WtfsPerDayAnalyzer calculator = new WtfsPerDayAnalyzer()

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
		GraphPoint<Double> timelinePoint = calculator.analyzeIdeaFlowStory(builder.buildTaskTimeline(), [])

		then:
		assert timelinePoint.metricType == MetricType.WTFS_PER_DAY
		assert timelinePoint.value == 2.0D

	}

	def "calculateMetrics SHOULD average the WTFs per 6hr day"() {

		given:
		builder.activate()
				.advanceMinutes(15)
				.wtf()
				.advanceHours(5)
				.wtf()
				.advanceHours(3)
				.wtf()
				.wtf()
				.awesome()
				.deactivate()

		when:
		GraphPoint<Double> timelinePoint = calculator.analyzeIdeaFlowStory(builder.buildTaskTimeline(), [])

		then:
		assert timelinePoint.metricType == MetricType.WTFS_PER_DAY
		assert timelinePoint.value == 2.91D

	}
}

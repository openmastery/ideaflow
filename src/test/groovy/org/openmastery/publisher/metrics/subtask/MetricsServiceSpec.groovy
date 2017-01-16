package org.openmastery.publisher.metrics.subtask

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimelineBuilder
import org.openmastery.publisher.api.journey.IdeaFlowStory
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.ideaflow.story.IdeaFlowStoryGenerator
import org.openmastery.storyweb.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.publisher.ideaflow.timeline.JourneyTimeline
import org.openmastery.publisher.ideaflow.story.TroubleshootingJourneyGenerator
import org.openmastery.storyweb.core.MetricsService
import org.openmastery.storyweb.core.metrics.spc.MetricSet
import org.openmastery.time.MockTimeService
import spock.lang.Specification

class MetricsServiceSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineBuilder builder = new IdeaFlowTimelineBuilder(mockTimeService)

	private TroubleshootingJourneyGenerator journeyGenerator = new TroubleshootingJourneyGenerator()
	private IdeaFlowStoryGenerator storyGenerator = new IdeaFlowStoryGenerator()
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
				.start(mockTimeService.now())
				.end(mockTimeService.hoursInFuture(1))
				.build()

		builder.activate()
		builder.subtask()
		builder.wtf()
		builder.troubleshootingHours(1)
		builder.advanceMinutes(10)
		builder.execute()
		builder.advanceMinutes(10)
		builder.execute()
		builder.advanceMinutes(30)
		builder.execute()
		builder.advanceMinutes(10)
		builder.awesome()


		when:
		IdeaFlowStory story = storyGenerator.generateIdeaFlowStory(builder.buildTaskTimeline())

		MetricSet metricSet = metricsService.generateMetricsForTask(story)

		then:
		assert metricSet != null
		assert metricSet.getFlattenedMetrics().size() == 15

		Metric maxCyclesMetric = metricSet.getFlattenedMetrics().find() { Metric metric -> metric.type == MetricType.MAX_EXPERIMENT_CYCLES }
		assert maxCyclesMetric.value == 3

		Metric maxHumanRatio = metricSet.getFlattenedMetrics().find() { Metric metric -> metric.type == MetricType.AVG_HUMAN_CYCLE_RATIOS }
		assert maxHumanRatio.value == new DurationInSeconds((long)50/3 * 60)

	}

}

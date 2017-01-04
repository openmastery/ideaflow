package org.openmastery.publisher.metrics.analyzer

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.metrics.GraphPoint
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.time.MockTimeService
import spock.lang.Specification


class HaystackAnalyzerSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	private calculator = new HaystackAnalyzer()

	LocalDateTime start

	def setup() {
		start = mockTimeService.now()
	}

	def "analyzeTimeline SHOULD identify the biggest consecutive time without execution within a band"() {
		given:
		IdeaFlowBand progressBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.PROGRESS)
				.relativePositionInSeconds(0)
				.durationInSeconds(30)
				.build()
		ExecutionEvent event = new ExecutionEvent(relativePositionInSeconds: 2)

		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [progressBand], executionEvents: [event])
		List<GraphPoint<DurationInSeconds>> graphPoints = calculator.analyzeTimelineAndJourneys(timeline, [])

		then:
		assert graphPoints.size() == 1
		assert  graphPoints.get(0).metricType == MetricType.HAYSTACK_SIZE
		assert graphPoints.get(0).value == new DurationInSeconds(28)
	}

	def "analyzeTimeline SHOULD return entire interval when no execution events"() {
		given:
		IdeaFlowBand progressBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.PROGRESS)
				.relativePositionInSeconds(0)
				.durationInSeconds(30)
				.build()

		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [progressBand], executionEvents: [])
		List<GraphPoint<DurationInSeconds>> graphPoints = calculator.analyzeTimelineAndJourneys(timeline, [])

		then:
		assert graphPoints.size() == 1
		assert  graphPoints.get(0).metricType == MetricType.HAYSTACK_SIZE
		assert graphPoints.get(0).value ==  new DurationInSeconds(30)

	}


	def "analyzeTimeline SHOULD identify the biggest consecutive time across consecutive bands"() {
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
		List<GraphPoint<DurationInSeconds>> graphPoints = calculator.analyzeTimelineAndJourneys(timeline, [])

		then:
		assert graphPoints.size() == 1
		assert  graphPoints.get(0).metricType == MetricType.HAYSTACK_SIZE
		assert graphPoints.get(0).value ==  new DurationInSeconds(88)

	}

	def "analyzeTimeline SHOULD restart the count when there are time gaps"() {
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
		List<GraphPoint<DurationInSeconds>> graphPoints = calculator.analyzeTimelineAndJourneys(timeline, [])

		then:
		assert graphPoints.size() == 1
		assert  graphPoints.get(0).metricType == MetricType.HAYSTACK_SIZE
		assert graphPoints.get(0).value ==  new DurationInSeconds(28)

	}

	def "analyzeTimeline SHOULD ignore learning bands"() {
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
		List<GraphPoint<DurationInSeconds>> graphPoints = calculator.analyzeTimelineAndJourneys(timeline, [])

		then:
		assert graphPoints.size() == 1
		assert  graphPoints.get(0).metricType == MetricType.HAYSTACK_SIZE
		assert graphPoints.get(0).value ==  new DurationInSeconds(55)
	}


	def "assignBlame SHOULD assign haystack to journey immediately after the haystack"() {
		given:
		IdeaFlowBand progressBand = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.PROGRESS)
				.relativePositionInSeconds(0)
				.durationInSeconds(30)
				.build()
		IdeaFlowBand troubleshootingBand1 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(30)
				.durationInSeconds(15)
				.build()
		IdeaFlowBand progressBand2 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.PROGRESS)
				.relativePositionInSeconds(45)
				.durationInSeconds(50)
				.build()
		IdeaFlowBand troubleshootingBand2 = IdeaFlowBand.builder()
				.type(IdeaFlowStateType.TROUBLESHOOTING)
				.relativePositionInSeconds(95)
				.durationInSeconds(15)
				.build()
		ExecutionEvent event = new ExecutionEvent(relativePositionInSeconds: 2)

		TroubleshootingJourney journey1 = new TroubleshootingJourney(troubleshootingBand1)
		journey1.relativePath = "/journey/1"
		journey1.id = 1

		TroubleshootingJourney journey2 = new TroubleshootingJourney(troubleshootingBand2)
		journey2.relativePath = "/journey/2"
		journey2.id = 2

		when:
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [progressBand, troubleshootingBand1, progressBand2, troubleshootingBand2], executionEvents: [event])
		List<GraphPoint<DurationInSeconds>> graphPoints = calculator.analyzeTimelineAndJourneys(timeline, [journey1, journey2])

		then:
		assert graphPoints.size() == 3

		assert graphPoints.get(0).relativePath == "/timeline"
		assert graphPoints.get(0).value == new DurationInSeconds(108)

		assert graphPoints.get(1).relativePath == "/journey/1"
		assert graphPoints.get(1).value == new DurationInSeconds(43)

		assert graphPoints.get(2).relativePath == "/journey/2"
		assert graphPoints.get(2).value == new DurationInSeconds(108)



	}



}

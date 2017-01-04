package org.openmastery.publisher.metrics.calculator

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.metrics.CapacityDistribution
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


	def "calculateCapacityDistribution SHOULD add up the duration for multiple bands of same type"() {
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
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [troubleshootingBand1, troubleshootingBand2])
		CapacityDistribution value = calculator.calculateCapacityDistribution(timeline)

		then:
		assert value.capacityDistributionByType.get(IdeaFlowStateType.TROUBLESHOOTING, 45)
	}

	def "calculateCapacityDistribution SHOULD add up the duration for bands by type"() {
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
		IdeaFlowTaskTimeline timeline = new IdeaFlowTaskTimeline(ideaFlowBands: [learningBand, progressBand, troubleshootingBand])
		CapacityDistribution value = calculator.calculateCapacityDistribution(timeline)

		then:
		assert value.capacityDistributionByType.get(IdeaFlowStateType.LEARNING, 30)
		assert value.capacityDistributionByType.get(IdeaFlowStateType.PROGRESS, 40)
		assert value.capacityDistributionByType.get(IdeaFlowStateType.TROUBLESHOOTING, 15)
	}


}

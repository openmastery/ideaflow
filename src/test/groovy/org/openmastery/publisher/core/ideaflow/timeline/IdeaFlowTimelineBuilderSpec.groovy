package org.openmastery.publisher.core.ideaflow.timeline

import org.joda.time.Duration
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel
import org.openmastery.time.MockTimeService
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

class IdeaFlowTimelineBuilderSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private long taskId = aRandom.intBetween(1, 100000)


	private IdeaFlowTimelineBuilder timelineBuilder = new IdeaFlowTimelineBuilder()

	def setup() {

	}


	def "generateProgressBands SHOULD create a progress band for an activate/deactivate interval"() {
		given:
		EventEntity taskStart = aRandom.eventEntity().type(EventType.ACTIVATE).position(mockTimeService.javaNow()).build()
		EventEntity taskEnd = aRandom.eventEntity().type(EventType.DEACTIVATE).position(mockTimeService.javaInFuture(3)).build()

		when:
		timelineBuilder.events([taskStart, taskEnd])
		List<IdeaFlowBandModel> progressBands = timelineBuilder.generateProgressBands()

		then:
		assert progressBands.size() == 1
		assert progressBands[0].getDuration() == Duration.standardHours(3)
	}

	def "generateProgressBands SHOULD create bands WHEN out of order intervals"() {
		given:
		EventEntity taskStart1 = aRandom.eventEntity().type(EventType.ACTIVATE).position(mockTimeService.javaNow()).build()
		EventEntity taskEnd1 = aRandom.eventEntity().type(EventType.DEACTIVATE).position(mockTimeService.javaInFuture(3)).build()
		EventEntity taskStart2 = aRandom.eventEntity().type(EventType.ACTIVATE).position(mockTimeService.javaInFuture(4)).build()
		EventEntity taskEnd2 = aRandom.eventEntity().type(EventType.DEACTIVATE).position(mockTimeService.javaInFuture(6)).build()

		when:
		timelineBuilder.events([taskStart1, taskEnd2, taskStart2, taskEnd1])
		List<IdeaFlowBandModel> progressBands = timelineBuilder.generateProgressBands()

		then:
		assert progressBands.size() == 2
		assert progressBands[0].getDuration() == Duration.standardHours(3)
		assert progressBands[1].getDuration() == Duration.standardHours(2)
	}

	//in actuality, this should probably prompt a "repair" job, looking at raw activity and creating the missing event
	def "generateProgressBands SHOULD ignore multiple activates in a row"() {
		EventEntity taskStart = aRandom.eventEntity().type(EventType.ACTIVATE).position(mockTimeService.javaNow()).build()
		EventEntity taskStartAgain = aRandom.eventEntity().type(EventType.ACTIVATE).position(mockTimeService.javaInFuture(1)).build()
		EventEntity taskEnd = aRandom.eventEntity().type(EventType.DEACTIVATE).position(mockTimeService.javaInFuture(5)).build()

		when:
		timelineBuilder.events([taskStart, taskStartAgain, taskEnd])
		List<IdeaFlowBandModel> progressBands = timelineBuilder.generateProgressBands()

		then:
		assert progressBands.size() == 1
		assert progressBands[0].getDuration() == Duration.standardHours(5)
	}

}

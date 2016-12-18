package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.Duration
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel
import org.openmastery.time.MockTimeService
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

class IdeaFlowTimelineBuilderSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineBuilder timelineBuilder = new IdeaFlowTimelineBuilder()

	private EventEntity createEvent(EventType type, int hoursInFuture) {
		aRandom.eventEntity().type(type).position(mockTimeService.javaInFuture(hoursInFuture)).build()
	}

	def "generateProgressBands SHOULD create a progress band for an activate/deactivate interval"() {
		given:
		EventEntity taskStart = createEvent(EventType.ACTIVATE, 0)
		EventEntity taskEnd = createEvent(EventType.DEACTIVATE, 3)

		when:
		timelineBuilder.events([taskStart, taskEnd])
		List<IdeaFlowBandModel> progressBands = timelineBuilder.generateProgressBands()

		then:
		assert progressBands.size() == 1
		assert progressBands[0].getDuration() == Duration.standardHours(3)
	}

	def "generateProgressBands SHOULD create bands WHEN out of order intervals"() {
		given:
		EventEntity taskStart1 = createEvent(EventType.ACTIVATE, 0)
		EventEntity taskEnd1 = createEvent(EventType.DEACTIVATE, 3)
		EventEntity taskStart2 = createEvent(EventType.ACTIVATE, 4)
		EventEntity taskEnd2 = createEvent(EventType.DEACTIVATE, 6)

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
		EventEntity taskStart = createEvent(EventType.ACTIVATE, 0)
		EventEntity taskStartAgain = createEvent(EventType.ACTIVATE, 1)
		EventEntity taskEnd = createEvent(EventType.DEACTIVATE, 5)

		when:
		timelineBuilder.events([taskStart, taskStartAgain, taskEnd])
		List<IdeaFlowBandModel> progressBands = timelineBuilder.generateProgressBands()

		then:
		assert progressBands.size() == 1
		assert progressBands[0].getDuration() == Duration.standardHours(5)
	}

}

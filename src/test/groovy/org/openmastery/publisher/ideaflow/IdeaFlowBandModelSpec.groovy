package org.openmastery.publisher.ideaflow

import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.time.MockTimeService
import spock.lang.Specification

class IdeaFlowBandModelSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()

	def "getDuration should subtract idle time"() {
		given:
		IdleTimeBandModel idleBand = IdleTimeBandModel.builder()
				.start(mockTimeService.now().plusHours(1))
				.end(mockTimeService.now().plusHours(2))
				.build()
		IdeaFlowBandModel ideaFlowBandModel = IdeaFlowBandModel.builder()
				.start(mockTimeService.now())
				.end(mockTimeService.now().plusHours(5))
				.idleBands([idleBand])
				.build()

		expect:
		assert ideaFlowBandModel.duration.toHours() == 4
	}

}

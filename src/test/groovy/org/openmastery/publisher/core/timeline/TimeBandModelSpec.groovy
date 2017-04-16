package org.openmastery.publisher.core.timeline

import java.time.Duration
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel
import spock.lang.Specification

class TimeBandModelSpec extends Specification {

	def "sumDuration SHOULD throw exception if any duration is negative"() {

		given:
		IdeaFlowBandModel model = Mock(IdeaFlowBandModel)
		model.getDuration() >> Duration.ofSeconds(-10)

		when:
		TimeBandModel.sumDuration([model])

		then:
		thrown(TimeBandModel.BandDurationIsNegativeException)

	}

	def "sumDuration SHOULD add up band durations"() {

		given:
		IdeaFlowBandModel model = Mock(IdeaFlowBandModel)
		model.getDuration() >> Duration.ofSeconds(10)

		when:
		Duration duration = TimeBandModel.sumDuration([model, model])

		then:
		duration == Duration.ofSeconds(20)

	}


}

package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowBand
import org.ideaflow.publisher.api.TimeBandTestSupport
import org.ideaflow.publisher.core.MockTimeService
import org.ideaflow.publisher.core.activity.IdleActivityEntity
import org.ideaflow.publisher.core.ideaflow.TimeBandCalculator
import spock.lang.Specification

import java.time.Duration

class TimeBandCalculatorSpec extends Specification implements TimeBandTestSupport {

	def time = new MockTimeService()

	def "getIdleDurationForTimeBand SHOULD include entire duration if idle is within band"() {
		given:
		IdeaFlowBand band =
				createBand(time.now(), time.inFuture(5))

		IdleActivityEntity idle =
				createIdle(time.inFuture(1), time.inFuture(3))

		expect:
		assert Duration.ofHours(2) == TimeBandCalculator.getIdleDurationForTimeBand(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time before the band"() {
		given:
		IdeaFlowBand band =
				createBand(time.inFuture(2), time.inFuture(5))

		IdleActivityEntity idle =
				createIdle(time.now(), time.inFuture(3))

		expect:
		assert Duration.ofHours(1) == TimeBandCalculator.getIdleDurationForTimeBand(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time after the band"() {
		given:
		IdeaFlowBand band =
				createBand(time.now(), time.inFuture(5))

		IdleActivityEntity idle =
				createIdle(time.inFuture(2), time.inFuture(6))

		expect:
		assert Duration.ofHours(3) == TimeBandCalculator.getIdleDurationForTimeBand(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time that falls before AND after the band"() {
		given:

		IdeaFlowBand band =
				createBand(time.inFuture(1), time.inFuture(5))

		IdleActivityEntity idle =
				createIdle(time.now(), time.inFuture(6))

		expect:
		assert Duration.ofHours(4) == TimeBandCalculator.getIdleDurationForTimeBand(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD provide total idle duration when multiple idles within band"() {
		given:

		IdeaFlowBand band =
				createBand(time.inFuture(5), time.inFuture(3))

		IdleActivityEntity idle =
				createIdle(time.inFuture(1), time.inFuture(2))

		expect:
		assert Duration.ofHours(4) == TimeBandCalculator.getIdleDurationForTimeBand(band, idle)
	}
}

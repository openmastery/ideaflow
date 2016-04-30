package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.IdeaFlowBand
import org.ideaflow.publisher.api.TimeBandTestSupport
import org.ideaflow.publisher.core.MockTimeService
import org.ideaflow.publisher.core.activity.IdleTimeBand
import org.ideaflow.publisher.core.timeline.TimeBandIdleCalculator
import spock.lang.Specification

import java.time.LocalDateTime

class TimeBandIdleCalculatorSpec extends Specification implements TimeBandTestSupport {

	MockTimeService time = new MockTimeService()
	TimeBandIdleCalculator calculator = new TimeBandIdleCalculator()
	LocalDateTime hourZero = time.now()
	LocalDateTime hourOne = time.inFuture(1)
	LocalDateTime hourTwo = time.inFuture(2)
	LocalDateTime hourThree = time.inFuture(3)
	LocalDateTime hourFour = time.inFuture(4)
	LocalDateTime hourFive = time.inFuture(5)
	LocalDateTime hourSix = time.inFuture(6)

	def "getIdleDurationForTimeBand SHOULD include entire duration if idle is within band"() {
		given:
		IdeaFlowBand band = createBand(hourZero, hourFive)
		IdleTimeBand idle = createIdle(hourOne, hourThree)

		expect:
		assert idle == calculator.getIdleForTimeBandOrNull(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time before the band"() {
		given:
		IdeaFlowBand band = createBand(hourTwo, hourFive)
		IdleTimeBand idle = createIdle(hourZero, hourThree)

		expect:
		IdleTimeBand expectedBand = IdleTimeBand.from(idle).start(hourTwo).build()
		assert expectedBand == calculator.getIdleForTimeBandOrNull(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time after the band"() {
		given:
		IdeaFlowBand band = createBand(hourZero, hourFive)
		IdleTimeBand idle = createIdle(hourTwo, hourSix)

		expect:
		IdleTimeBand expectedBand = IdleTimeBand.from(idle).end(hourFive).build()
		assert expectedBand == calculator.getIdleForTimeBandOrNull(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time that falls after the band end"() {
		given:
		IdeaFlowBand band = createBand(time.now(), time.inFuture(1))
		IdleTimeBand idle = createIdle(time.inFuture(2), time.inFuture(3))

		expect:
		assert null == calculator.getIdleForTimeBandOrNull(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time that falls before the band start"() {
		given:
		IdeaFlowBand band = createBand(time.inFuture(2), time.inFuture(3))
		IdleTimeBand idle = createIdle(time.now(), time.inFuture(1))

		expect:
		assert null == calculator.getIdleForTimeBandOrNull(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time that falls before AND after the band"() {
		given:
		IdeaFlowBand band = createBand(hourOne, hourFive)
		IdleTimeBand idle = createIdle(hourZero, hourSix)

		expect:
		IdleTimeBand expectedBand = IdleTimeBand.from(idle).start(hourOne).end(hourFive).build()
		assert expectedBand == calculator.getIdleForTimeBandOrNull(band, idle)
	}

}

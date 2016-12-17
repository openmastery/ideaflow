package org.openmastery.publisher.core.timeline

import org.joda.time.LocalDateTime
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.api.TimeBandTestSupport
import org.openmastery.time.MockTimeService
import spock.lang.Specification

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
		IdeaFlowBandModel band = createBand(hourZero, hourFive)
		IdleTimeBandModel idle = createIdle(hourOne, hourThree)

		expect:
		assert idle == calculator.getIdleForTimeBandOrNull(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time before the band"() {
		given:
		IdeaFlowBandModel band = createBand(hourTwo, hourFive)
		IdleTimeBandModel idle = createIdle(hourZero, hourThree)

		expect:
		IdleTimeBandModel expectedBand = IdleTimeBandModel.from(idle).start(hourTwo).build()
		assert expectedBand == calculator.getIdleForTimeBandOrNull(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time after the band"() {
		given:
		IdeaFlowBandModel band = createBand(hourZero, hourFive)
		IdleTimeBandModel idle = createIdle(hourTwo, hourSix)

		expect:
		IdleTimeBandModel expectedBand = IdleTimeBandModel.from(idle).end(hourFive).build()
		assert expectedBand == calculator.getIdleForTimeBandOrNull(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time that falls after the band end"() {
		given:
		IdeaFlowBandModel band = createBand(time.now(), time.inFuture(1))
		IdleTimeBandModel idle = createIdle(time.inFuture(2), time.inFuture(3))

		expect:
		assert null == calculator.getIdleForTimeBandOrNull(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time that falls before the band start"() {
		given:
		IdeaFlowBandModel band = createBand(time.inFuture(2), time.inFuture(3))
		IdleTimeBandModel idle = createIdle(time.now(), time.inFuture(1))

		expect:
		assert null == calculator.getIdleForTimeBandOrNull(band, idle)
	}

	def "getIdleDurationForTimeBand SHOULD ignore idle time that falls before AND after the band"() {
		given:
		IdeaFlowBandModel band = createBand(hourOne, hourFive)
		IdleTimeBandModel idle = createIdle(hourZero, hourSix)

		expect:
		IdleTimeBandModel expectedBand = IdleTimeBandModel.from(idle).start(hourOne).end(hourFive).build()
		assert expectedBand == calculator.getIdleForTimeBandOrNull(band, idle)
	}

}

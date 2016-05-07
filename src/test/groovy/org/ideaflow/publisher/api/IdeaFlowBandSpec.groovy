package org.ideaflow.publisher.api

import org.ideaflow.publisher.api.ideaflow.IdeaFlowBand
import org.ideaflow.publisher.core.MockTimeService
import spock.lang.Specification

import java.time.LocalDateTime

public class IdeaFlowBandSpec extends Specification implements TimeBandTestSupport {

	private MockTimeService timeService = new MockTimeService()
	private LocalDateTime hourZero = timeService.now()
	private LocalDateTime hourOne = timeService.plusHour().now()
	private LocalDateTime hourTwo = timeService.plusHour().now()
	private LocalDateTime hourThree = timeService.plusHour().now()
	private LocalDateTime hourFour = timeService.plusHour().now()
	private LocalDateTime hourFive = timeService.plusHour().now()
	private LocalDateTime hourSix = timeService.plusHour().now()


	def "splitAndReturn should return null if position is on or outside timeband range AND exclusive direction"() {
		given:
		IdeaFlowBand band = createBand(hourOne, hourTwo)

		expect:
		assert band.splitAndReturnLeftSide(hourZero) == null
		assert band.splitAndReturnLeftSide(hourOne) == null

		and:
		assert band.splitAndReturnRightSide(hourTwo) == null
		assert band.splitAndReturnRightSide(hourThree) == null
	}

	def "splitAndReturn should return self WHEN position is on or outside timeband range AND inclusive direction"() {
		given:
		IdeaFlowBand band = createBand(hourOne, hourTwo)

		expect:
		assert band.splitAndReturnLeftSide(hourTwo).is(band)
		assert band.splitAndReturnLeftSide(hourThree).is(band)

		and:
		assert band.splitAndReturnRightSide(hourZero).is(band)
		assert band.splitAndReturnRightSide(hourOne).is(band)
	}

	def "spiltAndReturn should split timeband WHEN position is within timeband range"() {
		given:
		IdeaFlowBand band = createBand(hourOne, hourThree)

		when:
		IdeaFlowBand leftSide = band.splitAndReturnLeftSide(hourTwo)

		then:
		assertStartAndEnd(leftSide, hourOne, hourTwo)

		when:
		IdeaFlowBand rightSide = band.splitAndReturnRightSide(hourTwo)

		then:
		assertStartAndEnd(rightSide, hourTwo, hourThree)
	}

	def "splitAndReturn should split nested bands"() {
		given:
		IdeaFlowBand outerBand = createBand(hourOne, hourSix)
		IdeaFlowBand nestedBand1 = createBand(hourTwo, hourThree)
		IdeaFlowBand nestedBand2 = createBand(hourFour, hourFive)
		IdeaFlowBand nestedBand3 = createBand(hourFive, hourSix)
		outerBand.nestedBands = [nestedBand1, nestedBand2, nestedBand3]
		LocalDateTime hourFourOneHalf = hourFour.plusMinutes(30)

		when:
		IdeaFlowBand leftSide = outerBand.splitAndReturnLeftSide(hourFourOneHalf)

		then:
		assertStartAndEnd(leftSide, hourOne, hourFourOneHalf)
		assertStartAndEnd(leftSide.nestedBands[0], hourTwo, hourThree)
		assertStartAndEnd(leftSide.nestedBands[1], hourFour, hourFourOneHalf)

		when:
		IdeaFlowBand rightSide = outerBand.splitAndReturnRightSide(hourFour.plusMinutes(30))

		then:
		assertStartAndEnd(rightSide, hourFourOneHalf, hourSix)
		assertStartAndEnd(rightSide.nestedBands[0], hourFourOneHalf, hourFive)
		assertStartAndEnd(rightSide.nestedBands[1], hourFive, hourSix)
	}

}
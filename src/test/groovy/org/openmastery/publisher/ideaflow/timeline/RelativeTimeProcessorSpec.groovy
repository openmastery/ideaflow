package org.openmastery.publisher.ideaflow.timeline

import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.PositionableAndIntervalListBuilder
import org.openmastery.publisher.api.PositionableComparator
import spock.lang.Specification

import java.time.Duration

class RelativeTimeProcessorSpec extends Specification {

	PositionableAndIntervalListBuilder builder = new PositionableAndIntervalListBuilder()

	private List<Positionable> processRelativeTime() {
		List<Positionable> positionables = builder.buildPositionables()
		new RelativeTimeProcessor().computeRelativeTime(positionables)
		positionables.sort(false, PositionableComparator.INSTANCE)
	}

	private long hoursToSeconds(int hours) {
		Duration.ofHours(hours).seconds
	}

	def "should adjust instant when idle is before positionable"() {
		given:
		builder.interval(0, 6)
				.idle(1, 2)
				.position(4)

		when:
		List<Positionable> positionables = processRelativeTime()

		then:
		assert positionables[0].relativePositionInSeconds == 0l
		assert positionables[1].relativePositionInSeconds == hoursToSeconds(1)
		assert positionables[2].relativePositionInSeconds == hoursToSeconds(3)
	}

	def "should ignore idle when idle is after positionable"() {
		given:
		builder.interval(0, 6)
				.position(2)
				.idle(4, 5)

		when:
		List<Positionable> positionables = processRelativeTime()

		then:
		assert positionables[0].relativePositionInSeconds == 0l
		assert positionables[1].relativePositionInSeconds == hoursToSeconds(2)
		assert positionables[2].relativePositionInSeconds == hoursToSeconds(4)
	}

	def "should collapse idle time when idle starts at interval start"() {
		given:
		builder.interval(0, 6)
				.idle(0, 3)
				.interval(6, 8)

		when:
		List<Positionable> positionables = processRelativeTime()

		then:
		assert positionables[0].relativePositionInSeconds == 0l
		assert positionables[1].relativePositionInSeconds == hoursToSeconds(0)
		assert positionables[2].relativePositionInSeconds == hoursToSeconds(3)
	}

	def "should collapse idle time when idle starts at interval end"() {
		given:
		builder.interval(0, 6)
				.idle(3, 6)
				.interval(6, 8)

		when:
		List<Positionable> positionables = processRelativeTime()

		then:
		assert positionables[0].relativePositionInSeconds == 0l
		assert positionables[1].relativePositionInSeconds == hoursToSeconds(3)
		assert positionables[2].relativePositionInSeconds == hoursToSeconds(3)
	}

	def "should collapse idle time if consecutive idle"() {
		given:
		builder.interval(0, 6)
				.idle(2, 3)
				.idle(3, 4)
				.interval(6, 8)

		when:
		List<Positionable> positionables = processRelativeTime()

		then:
		assert positionables[0].relativePositionInSeconds == 0l
		assert positionables[1].relativePositionInSeconds == hoursToSeconds(2)
		assert positionables[2].relativePositionInSeconds == hoursToSeconds(2)
		assert positionables[3].relativePositionInSeconds == hoursToSeconds(4)
	}

	def "should not collapse empty space if interval starts at interval end"() {
		given:
		builder.interval(0, 2)
				.interval(2, 4)
				.interval(4, 6)

		when:
		List<Positionable> positionables = processRelativeTime()

		then:
		assert positionables[0].relativePositionInSeconds == 0l
		assert positionables[1].relativePositionInSeconds == hoursToSeconds(2)
		assert positionables[2].relativePositionInSeconds == hoursToSeconds(4)
	}

	def "should set relative time to start of idle if positionable within idle band"() {
		given:
		builder.interval(0, 8)
				.idle(2, 6)
				.position(3)
				.position(5)
				.position(7)

		when:
		List<Positionable> positionables = processRelativeTime()

		then:
		assert positionables[0].relativePositionInSeconds == 0l
		assert positionables[1].relativePositionInSeconds == hoursToSeconds(2)
		assert positionables[2].relativePositionInSeconds == hoursToSeconds(2)
		assert positionables[3].relativePositionInSeconds == hoursToSeconds(2)
		assert positionables[4].relativePositionInSeconds == hoursToSeconds(3)
		assert positionables.size() == 5
	}

}

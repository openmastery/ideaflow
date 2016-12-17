package org.openmastery.time

import java.time.LocalDateTime

class MockTimeService implements TimeService {

	private LocalDateTime now

	MockTimeService() {
		now = LocalDateTime.of(2016, 1, 1, 0, 0)
	}

	@Override
	LocalDateTime javaNow() {
		return now
	}

	LocalDateTime javaInFuture(int hours) {
		now.plusHours(hours)
	}

	@Override
	org.joda.time.LocalDateTime now() {
		return TimeConverter.toJodaLocalDateTime(javaNow())
	}

	org.joda.time.LocalDateTime inFuture(int hours) {
		TimeConverter.toJodaLocalDateTime(now.plusHours(hours))
	}

	MockTimeService plusHour() {
		plusHours(1)
		this
	}

	MockTimeService plusHours(int hours) {
		now = now.plusHours(hours)
		this
	}

	MockTimeService plusMinutes(int minutes) {
		now = now.plusMinutes(minutes)
		this
	}

	MockTimeService plusSeconds(int seconds) {
		now = now.plusSeconds(seconds)
		this
	}

	MockTimeService advanceTime(int hours, int minutes, int seconds) {
		plusHours(hours)
		plusMinutes(minutes)
		plusSeconds(seconds)
	}

}

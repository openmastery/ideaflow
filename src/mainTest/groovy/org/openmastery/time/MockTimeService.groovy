package org.openmastery.time

import java.time.LocalDateTime

class MockTimeService implements TimeService {

	private LocalDateTime now

	MockTimeService() {
		now = LocalDateTime.of(2016, 1, 1, 0, 0)
	}

	@Override
	LocalDateTime now() {
		now
	}

	LocalDateTime daysInFuture(int days) {
		now.plusDays(days)
	}

	LocalDateTime hoursInFuture(int hours) {
		now.plusHours(hours)
	}

	LocalDateTime minutesInFuture(int minutes) {
		now.plusMinutes(minutes)
	}

	LocalDateTime secondsInFuture(int seconds) {
		now.plusSeconds(seconds)
	}

	MockTimeService plusHour() {
		plusHours(1)
		this
	}

	MockTimeService plusDays(int days) {
		now = now.plusDays(days)
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

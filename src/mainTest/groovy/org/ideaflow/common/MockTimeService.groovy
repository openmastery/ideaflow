package org.ideaflow.common

import org.ideaflow.publisher.core.TimeService

import java.time.LocalDateTime

class MockTimeService implements TimeService {

	private LocalDateTime now

	MockTimeService() {
		now = LocalDateTime.of(2016, 1, 1, 0, 0)
	}

	@Override
	LocalDateTime now() {
		return now
	}

	LocalDateTime inFuture(int hours) {
		now.plusHours(hours)
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

package org.ideaflow.publisher.core

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

	MockTimeService plusHour() {
		plusHours(1)
		this
	}

	MockTimeService plusHours(int hours) {
		now = now.plusHours(hours)
		this
	}

	MockTimeService plusMinute() {
		plusMinutes(1)
		this
	}

	MockTimeService plusMinutes(int minutes) {
		now = now.plusMinutes(minutes)
		this
	}

}

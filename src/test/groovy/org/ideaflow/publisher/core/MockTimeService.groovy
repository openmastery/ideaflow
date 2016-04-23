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

	void plusHour() {
		plusHours(1)
	}

	void plusHours(int hours) {
		now = now.plusHours(hours)
	}

	void plusMinute() {
		plusMinutes(1)
	}

	void plusMinutes(int minutes) {
		now = now.plusMinutes(minutes)
	}

}

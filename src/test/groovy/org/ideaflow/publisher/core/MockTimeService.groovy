package org.ideaflow.publisher.core

import org.apache.tomcat.jni.Local

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

	MockTimeService plusMinute() {
		plusMinutes(1)
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



}

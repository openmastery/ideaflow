package org.openmastery.time

import org.joda.time.LocalDateTime

class MockTimeService implements TimeService {

	private LocalDateTime now

	MockTimeService() {
		now = TimeConverter.toJodaLocalDateTime(java.time.LocalDateTime.of(2016, 1, 1, 0, 0))
	}

	@Override
	java.time.LocalDateTime javaNow() {
		TimeConverter.toJavaLocalDateTime(now)
	}

	@Deprecated
	java.time.LocalDateTime javaInFuture(int hours) {
		javaHoursInFuture(hours)
	}

	java.time.LocalDateTime javaDaysInFuture(int days) {
		TimeConverter.toJavaLocalDateTime(daysInFuture(days))
	}

	java.time.LocalDateTime javaHoursInFuture(int hours) {
		TimeConverter.toJavaLocalDateTime(hoursInFuture(hours))
	}

	java.time.LocalDateTime javaMinutesInFuture(int minutes) {
		TimeConverter.toJavaLocalDateTime(minutesInFuture(minutes))
	}

	java.time.LocalDateTime javaSecondsInFuture(int seconds) {
		TimeConverter.toJavaLocalDateTime(secondsInFuture(seconds))
	}

	@Override
	LocalDateTime now() {
		now
	}

	@Deprecated
	LocalDateTime inFuture(int hours) {
		hoursInFuture(hours)
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

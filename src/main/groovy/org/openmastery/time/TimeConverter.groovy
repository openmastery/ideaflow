package org.openmastery.time

import java.time.LocalDateTime


class TimeConverter {

	public static org.joda.time.LocalDateTime toJodaLocalDateTime(LocalDateTime localDateTime) {
		new org.joda.time.LocalDateTime(
				localDateTime.getYear(),
				localDateTime.getMonthValue(),
				localDateTime.getDayOfMonth(),
				localDateTime.getHour(),
				localDateTime.getMinute(),
				localDateTime.getSecond())
	}

	public static LocalDateTime toJavaLocalDateTime(org.joda.time.LocalDateTime localDateTime) {
		LocalDateTime.of(
				localDateTime.getYear(),
				localDateTime.getMonthOfYear(),
				localDateTime.getDayOfMonth(),
				localDateTime.getHourOfDay(),
				localDateTime.getMinuteOfHour(),
				localDateTime.getSecondOfMinute())
	}

}

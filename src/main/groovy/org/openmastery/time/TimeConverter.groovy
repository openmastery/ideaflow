/*
 * Copyright 2017 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.time

import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime


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

	public static org.joda.time.LocalDateTime toJodaLocalDateTime(Timestamp timestamp) {
		new org.joda.time.LocalDateTime(timestamp.time)
	}

	public static LocalDateTime toJavaLocalDateTime(Timestamp timestamp) {
		if (timestamp == null) {
			return null
		}
		LocalDateTime.of(timestamp.getYear(),
				timestamp.getMonthOfYear(),
				timestamp.getDayOfMonth(),
				timestamp.getHourOfDay(),
				timestamp.getMinuteOfHour(),
				timestamp.getSecondOfMinute())
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

	public static ZonedDateTime toJavaDateTime(org.joda.time.LocalDateTime localDateTime) {
		LocalDateTime javaDateTime = toJavaLocalDateTime(localDateTime)
		ZonedDateTime.of(javaDateTime.toLocalDate(), javaDateTime.toLocalTime(), ZoneId.of("UTC"));
	}

	public static Timestamp toSqlTimestamp(org.joda.time.LocalDateTime localDateTime) {
		new Timestamp(localDateTime.getYear() -1900,
				localDateTime.getMonthOfYear()-1,
				localDateTime.getDayOfMonth(),
				localDateTime.getHourOfDay(),
				localDateTime.getMinuteOfHour(),
				localDateTime.getSecondOfMinute(), 0)
	}

	public static Duration between(org.joda.time.LocalDateTime start, org.joda.time.LocalDateTime end) {
		return new Duration(start.toDateTime(DateTimeZone.UTC), end.toDateTime(DateTimeZone.UTC))
	}

	public static Duration between(LocalDateTime start, LocalDateTime end) {
		return between(toJodaLocalDateTime(start), toJodaLocalDateTime(end));
	}

	static String formatDate(LocalDate localDate) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");

		return formatter.print(localDate)
	}

	static String formatDate(org.joda.time.LocalDateTime localDateTime) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");

		return formatter.print(localDateTime)
	}
}

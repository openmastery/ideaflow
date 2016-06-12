package org.openmastery.time;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Component
public class LocalDateTimeService implements TimeService {

	@Override
	public LocalDateTime now() {
		return nowTruncateToSeconds();
	}

	@Override
	public org.joda.time.LocalDateTime jodaNow() {
		return jodaNowTruncateToSeconds();
	}

	public static LocalDateTime nowTruncateToSeconds() {
		return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	public static org.joda.time.LocalDateTime jodaNowTruncateToSeconds() {
		org.joda.time.LocalDateTime localDateTime = org.joda.time.LocalDateTime.now();
		return localDateTime.minusMillis(localDateTime.getMillisOfSecond());
	}

}

package org.openmastery.time;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LocalDateTimeService implements TimeService {

	@Override
	public LocalDateTime now() {
		return LocalDateTime.now();
	}

}

package org.ideaflow.publisher.core;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RealTimeService implements TimeService {

	@Override
	public LocalDateTime now() {
		return LocalDateTime.now();
	}

}

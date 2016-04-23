package org.ideaflow.publisher.core;

import java.time.LocalDateTime;

public class RealTimeService implements TimeService {

	@Override
	public LocalDateTime now() {
		return LocalDateTime.now();
	}

}

package org.openmastery.publisher.api;

import java.time.Duration;
import java.time.LocalDateTime;

public interface Interval {

	LocalDateTime getStart();

	LocalDateTime getEnd();

	// TODO: change to seconds
	Duration getDuration();

}

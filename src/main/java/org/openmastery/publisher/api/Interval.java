package org.openmastery.publisher.api;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

public interface Interval {

	LocalDateTime getStart();

	LocalDateTime getEnd();

	// TODO: change to seconds
	Duration getDuration();

}

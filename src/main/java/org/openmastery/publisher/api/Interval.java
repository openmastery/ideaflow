package org.openmastery.publisher.api;

import java.time.Duration;
import java.time.LocalDateTime;

public interface Interval extends Positionable {

	LocalDateTime getStart();

	LocalDateTime getEnd();

	// TODO: change to seconds
	Duration getDuration();

	Long getDurationInSeconds();


	interface Factory<T extends Interval> {

		T create(T interval, LocalDateTime start, LocalDateTime end, Long relativePositionInSeconds, Long durationInSeconds);

	}

}

package org.openmastery.publisher.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.openmastery.time.TimeConverter;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntervalImpl implements Interval, Positionable {

	private LocalDateTime start;
	private LocalDateTime end;
	private Long relativePositionInSeconds;

	@Override
	public Duration getDuration() {
		return TimeConverter.between(start, end);
	}

	@Override
	public LocalDateTime getPosition() {
		return start;
	}

}

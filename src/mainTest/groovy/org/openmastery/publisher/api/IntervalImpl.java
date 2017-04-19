package org.openmastery.publisher.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntervalImpl implements Interval, RelativePositionable {

	private LocalDateTime start;
	private LocalDateTime end;
	private Long relativePositionInSeconds;

	@Override
	public Duration getDuration() {
		return Duration.between(start, end);
	}

	@Override
	public Long getDurationInSeconds() {
		return getDuration().getSeconds();
	}

	@Override
	public LocalDateTime getPosition() {
		return start;
	}

}

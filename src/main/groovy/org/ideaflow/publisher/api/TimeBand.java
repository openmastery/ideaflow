package org.ideaflow.publisher.api;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class TimeBand<T extends TimeBand> {

	public abstract LocalDateTime getStart();

	public abstract LocalDateTime getEnd();

	public abstract Duration getDuration();

	public boolean contains(LocalDateTime position) {
			return (position.isAfter(getStart()) && position.isBefore(getEnd()))
					|| position.isEqual(getStart()) || position.isEqual(getEnd());
	}

	public boolean startsOnOrAfter(LocalDateTime position) {
		return getStart().equals(position) || getStart().isAfter(position);
	}

	public boolean endsOnOrBefore(LocalDateTime position) {
		return getEnd().equals(position) || getEnd().isBefore(position);
	}

	public abstract T splitAndReturnLeftSide(LocalDateTime position);

	public abstract T splitAndReturnRightSide(LocalDateTime position);


	public static Duration sumDuration(List<? extends TimeBand> ideaFlowBands) {
		Duration duration = Duration.ZERO;
		for (TimeBand ideaFlowBand : ideaFlowBands) {
			duration = duration.plus(ideaFlowBand.getDuration());
		}
		return duration;
	}

}

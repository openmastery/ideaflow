package org.ideaflow.publisher.api.timeline;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class TimeBand<T extends TimeBand> {

	private long relativeStart;

	public long getRelativeStart() {
		return relativeStart;
	}

	public void setRelativeStart(long relativeStart) {
		this.relativeStart = relativeStart;
	}

	public abstract LocalDateTime getStart();

	public abstract LocalDateTime getEnd();

	public abstract Duration getDuration();

	public abstract List<? extends TimeBand> getContainedBands();

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

	public final T splitAndReturnLeftSide(LocalDateTime position) {
		if (startsOnOrAfter(position)) {
			return null;
		} else if (endsOnOrBefore(position)) {
			return (T) this;
		} else {
			return internalSplitAndReturnLeftSide(position);
		}
	}

	public final T splitAndReturnRightSide(LocalDateTime position) {
		if (endsOnOrBefore(position)) {
			return null;
		} else if (startsOnOrAfter(position)) {
			return (T) this;
		} else {
			return internalSplitAndReturnRightSide(position);
		}
	}

	protected abstract T internalSplitAndReturnLeftSide(LocalDateTime position);

	protected abstract T internalSplitAndReturnRightSide(LocalDateTime position);

	public static <TB extends TimeBand> List<TB> splitAndReturnLeftSide(List<TB> timeBands, LocalDateTime position) {
		List<TB> splitTimeBands = new ArrayList<>();
		for (TB timeBand : timeBands) {
			TB splitTimeBand = (TB) timeBand.splitAndReturnLeftSide(position);
			if (splitTimeBand != null) {
				splitTimeBands.add(splitTimeBand);
			}
		}
		return splitTimeBands;
	}

	public static <TB extends TimeBand> List<TB> splitAndReturnRightSide(List<TB> timeBands, LocalDateTime position) {
		List<TB> splitTimeBands = new ArrayList<>();
		for (TB timeBand : timeBands) {
			TB splitTimeBand = (TB) timeBand.splitAndReturnRightSide(position);
			if (splitTimeBand != null) {
				splitTimeBands.add(splitTimeBand);
			}
		}
		return splitTimeBands;
	}

	public static Duration sumDuration(List<? extends TimeBand> ideaFlowBands) {
		Duration duration = Duration.ZERO;
		for (TimeBand ideaFlowBand : ideaFlowBands) {
			duration = duration.plus(ideaFlowBand.getDuration());
		}
		return duration;
	}

}

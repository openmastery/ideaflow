package org.openmastery.publisher.core.timeline;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class TimeBandModel<T extends TimeBandModel> {

	private long relativePositionInSeconds;

	public long getRelativePositionInSeconds() {
		return relativePositionInSeconds;
	}

	public void setRelativePositionInSeconds(long relativePositionInSeconds) {
		this.relativePositionInSeconds = relativePositionInSeconds;
	}

	public abstract LocalDateTime getStart();

	public abstract LocalDateTime getEnd();

	public abstract Duration getDuration();

	public abstract List<? extends TimeBandModel> getContainedBands();

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

	public static <TB extends TimeBandModel> List<TB> splitAndReturnLeftSide(List<TB> timeBands, LocalDateTime position) {
		List<TB> splitTimeBands = new ArrayList<>();
		for (TB timeBand : timeBands) {
			TB splitTimeBand = (TB) timeBand.splitAndReturnLeftSide(position);
			if (splitTimeBand != null) {
				splitTimeBands.add(splitTimeBand);
			}
		}
		return splitTimeBands;
	}

	public static <TB extends TimeBandModel> List<TB> splitAndReturnRightSide(List<TB> timeBands, LocalDateTime position) {
		List<TB> splitTimeBands = new ArrayList<>();
		for (TB timeBand : timeBands) {
			TB splitTimeBand = (TB) timeBand.splitAndReturnRightSide(position);
			if (splitTimeBand != null) {
				splitTimeBands.add(splitTimeBand);
			}
		}
		return splitTimeBands;
	}

	public static Duration sumDuration(List<? extends TimeBandModel> ideaFlowBands) {
		Duration duration = Duration.ZERO;
		for (TimeBandModel ideaFlowBand : ideaFlowBands) {
			Duration bandDuration = ideaFlowBand.getDuration();
			if (bandDuration.isNegative()) {
				throw new BandDurationIsNegativeException(ideaFlowBand);
			}
			duration = duration.plus(bandDuration);
		}
		return duration;
	}


	private static class BandDurationIsNegativeException extends RuntimeException {

		public BandDurationIsNegativeException(TimeBandModel ideaFlowBand) {
			super(ideaFlowBand.toString());
		}
	}
}

/**
 * Copyright 2016 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.core.timeline;

import org.joda.time.LocalDateTime;
import org.joda.time.Duration;
import org.openmastery.publisher.core.Positionable;

import java.util.ArrayList;
import java.util.List;

public abstract class TimeBandModel<T extends TimeBandModel> implements Positionable {

	private Long relativePositionInSeconds;

	public Long getRelativePositionInSeconds() {
		return relativePositionInSeconds;
	}

	public void setRelativePositionInSeconds(Long relativePositionInSeconds) {
		this.relativePositionInSeconds = relativePositionInSeconds;
	}

	public LocalDateTime getPosition() {
		return getStart();
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
			if (bandDuration.getMillis() < 0) {
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

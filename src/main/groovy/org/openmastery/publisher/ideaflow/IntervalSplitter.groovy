/*
 * Copyright 2017 New Iron Group, Inc.
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
package org.openmastery.publisher.ideaflow

import org.openmastery.publisher.api.Interval

import java.time.LocalDateTime

class IntervalSplitter<T extends Interval> {

	private LocalDateTime start
	private LocalDateTime end
	private Long durationInSeconds
	private Long relativePositionInSeconds
	private List<T> intervals
	private Interval.Factory<T> intervalFactory

	public IntervalSplitter<T> start(LocalDateTime start) {
		this.start = start
		this
	}

	public IntervalSplitter<T> end(LocalDateTime end) {
		this.end = end
		this
	}

	public IntervalSplitter<T> durationInSeconds(Long durationInSeconds) {
		this.durationInSeconds = durationInSeconds
		this
	}

	public IntervalSplitter<T> relativePositionInSeconds(Long relativePositionInSeconds) {
		this.relativePositionInSeconds = relativePositionInSeconds
		this
	}

	public IntervalSplitter<T> intervals(List<T> intervals) {
		this.intervals = intervals
		this
	}

	public IntervalSplitter<T> intervalFactory(Interval.Factory<T> intervalFactory) {
		this.intervalFactory = intervalFactory
		this
	}

	List<T> split() {
		// TODO: this assumes intervals is sorted, as well as relative time has been processed - should do that here
		List<T> intervalsToReturn = []
		for (T interval : intervals) {
			LocalDateTime intervalStart = interval.start
			LocalDateTime intervalEnd = interval.end

			boolean startsWithinRange = intervalStart.isEqual(start) || (intervalStart.isAfter(start) && intervalStart.isBefore(end))
			boolean endsWithinRange = intervalEnd.isEqual(end) || (intervalEnd.isBefore(end) && intervalEnd.isAfter(start))

			T intervalToReturn = null
			if (startsWithinRange && endsWithinRange) {
				intervalToReturn = interval
			} else {
				if (startsWithinRange) {
					Long intervalDurationInSeconds = durationInSeconds - (interval.relativePositionInSeconds - relativePositionInSeconds)
					intervalToReturn = intervalFactory.create(interval, intervalStart, end, interval.relativePositionInSeconds, intervalDurationInSeconds)
				} else if (endsWithinRange) {
					Long ideaFlowBandDuration = interval.durationInSeconds - (relativePositionInSeconds - interval.relativePositionInSeconds)
					intervalToReturn = intervalFactory.create(interval, start, intervalEnd, relativePositionInSeconds, ideaFlowBandDuration)
				} else if (start.isAfter(intervalStart) && end.isBefore(intervalEnd)) {
					intervalToReturn = intervalFactory.create(interval, start, end, relativePositionInSeconds, durationInSeconds)
				}
			}

			if (intervalToReturn != null) {
				intervalsToReturn.add(intervalToReturn)
			}
		}
		return intervalsToReturn
	}

}

package org.openmastery.publisher.core.ideaflow.timeline

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.Interval
import org.openmastery.publisher.api.IntervalComparator
import org.openmastery.publisher.core.timeline.IdleTimeBandModel


class IntervalGapGenerator {

	List<IdleTimeBandModel> generateIntervalGapsAsIdleTimeBands(List<Interval> intervalList) {
		List idleBands = []
		LocalDateTime maxIntervalEnd = null

		intervalList = intervalList.sort(false, IntervalComparator.INSTANCE)
		intervalList.each { Interval interval ->
			if (maxIntervalEnd == null) {
				maxIntervalEnd = interval.end
			} else {
				if (interval.start.isAfter(maxIntervalEnd)) {
					idleBands << IdleTimeBandModel.builder()
							.start(maxIntervalEnd)
							.end(interval.start)
							.build()
				}

				if (interval.end.isAfter(maxIntervalEnd)) {
					maxIntervalEnd = interval.end
				}
			}
		}
		idleBands
	}

}

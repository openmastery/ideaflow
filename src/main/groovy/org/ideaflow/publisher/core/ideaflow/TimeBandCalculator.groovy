package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.core.activity.IdleTimeBand

import java.time.Duration

class TimeBandCalculator {

	Duration getIdleDurationForTimeBand(TimeBand timeBand, IdleTimeBand idle) {
		Duration duration = Duration.ZERO
		if (idle.startsOnOrAfter(timeBand.start)) {
			if (idle.endsOnOrBefore(timeBand.end)) {
				// happy path
				duration = idle.duration
			} else if (idle.start.isBefore(timeBand.end)) {
				// idle overlaps end of time band
				duration = Duration.between(idle.start, timeBand.end)
			}
		} else if (idle.endsOnOrBefore(timeBand.end)) {
			// idle overlaps start of time band
			duration = Duration.between(timeBand.start, idle.end)
		}
		duration
	}

}

package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowBand
import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.core.activity.IdleActivityEntity

import java.time.Duration

class TimeBandCalculator {

static Duration getIdleDurationForTimeBand(TimeBand timeBand, IdleActivityEntity idle) {
		Duration duration = Duration.ZERO
		if (isIdleStartAfterOrEqualToTimeBandStart(timeBand, idle)) {
			if (isIdleEndBeforeOrEqualToTimeBandEnd(timeBand, idle)) {
				// happy path
				duration = idle.duration
			} else if (idle.start.isBefore(timeBand.end)) {
				// idle overlaps end of time band
				duration = Duration.between(idle.start, timeBand.end)
			}
		} else if (isIdleEndBeforeOrEqualToTimeBandEnd(timeBand, idle)) {
			// idle overlaps start of time band
			duration = Duration.between(timeBand.start, idle.end)
		}
		duration
	}

	static boolean isIdleStartAfterOrEqualToTimeBandStart(TimeBand timeBand, IdleActivityEntity idle) {
		idle.start.isAfter(timeBand.start) || idle.start == timeBand.start
	}

	static boolean isIdleEndBeforeOrEqualToTimeBandEnd(TimeBand timeBand, IdleActivityEntity idle) {
		idle.end.isBefore(timeBand.end) || idle.end == timeBand.end
	}
}

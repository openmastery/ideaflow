package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.IdleTimeBand
import org.ideaflow.publisher.api.TimeBand

import java.time.LocalDateTime

class TimeBandIdleCalculator {

	IdleTimeBand getIdleForTimeBandOrNull(TimeBand timeBand, IdleTimeBand idle) {
		IdleTimeBand splitIdle
		if (timeBand.startsOnOrAfter(idle.end) || timeBand.endsOnOrBefore(idle.start)) {
			splitIdle = null
		} else if (idle.startsOnOrAfter(timeBand.start)) {
			if (idle.endsOnOrBefore(timeBand.end)) {
				// happy path
				splitIdle = idle
			} else {
				// idle overlaps end of time band
				splitIdle = cloneIdleBand(idle, idle.start, timeBand.end)
			}
		} else if (idle.endsOnOrBefore(timeBand.end)) {
			// idle overlaps start of time band
			splitIdle = cloneIdleBand(idle, timeBand.start, idle.end)
		} else {
			splitIdle = cloneIdleBand(idle, timeBand.start, timeBand.end)
		}
		splitIdle
	}

	private static IdleTimeBand cloneIdleBand(IdleTimeBand source, LocalDateTime start, LocalDateTime end) {
		IdleTimeBand.from(source)
				.start(start)
				.end(end)
				.build()
	}

}

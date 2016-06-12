package org.openmastery.publisher.core.timeline

import java.time.LocalDateTime

class TimeBandIdleCalculator {

	IdleTimeBandModel getIdleForTimeBandOrNull(TimeBandModel timeBand, IdleTimeBandModel idle) {
		IdleTimeBandModel splitIdle
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

	private static IdleTimeBandModel cloneIdleBand(IdleTimeBandModel source, LocalDateTime start, LocalDateTime end) {
		IdleTimeBandModel.from(source)
				.start(start)
				.end(end)
				.build()
	}

}

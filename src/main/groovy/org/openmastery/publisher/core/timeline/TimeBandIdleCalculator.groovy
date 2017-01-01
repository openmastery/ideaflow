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
package org.openmastery.publisher.core.timeline

import org.joda.time.LocalDateTime

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

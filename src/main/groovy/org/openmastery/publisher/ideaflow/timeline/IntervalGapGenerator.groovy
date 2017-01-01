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
package org.openmastery.publisher.ideaflow.timeline

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

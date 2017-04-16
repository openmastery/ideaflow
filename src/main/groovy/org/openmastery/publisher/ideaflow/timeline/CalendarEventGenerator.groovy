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

import org.openmastery.publisher.api.Interval
import org.openmastery.publisher.api.IntervalComparator
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.core.timeline.IdleTimeBandModel

import java.time.LocalDateTime

class CalendarEventGenerator {

	List<Event> generateCalendarEvents(List<Interval> intervalList) {
		intervalList = intervalList.sort(false, IntervalComparator.INSTANCE)

		boolean addNextNonIdleInterval = false;
		Set<LocalDateTime> calendarEventPositions = []
		intervalList.each { Interval interval ->
			if (addNextNonIdleInterval) {
				if ((interval instanceof IdleTimeBandModel) == false) {
					addNextNonIdleInterval = false
					calendarEventPositions.add(interval.start)
				}
			}

			if (spansDay(interval)) {
				if (interval instanceof IdleTimeBandModel) {
					addNextNonIdleInterval = true;
				} else {
					calendarEventPositions.addAll(getStartOfDaysBetweenInterval(interval))
				}
			}
		}

		calendarEventPositions.collect { LocalDateTime position ->
			Event event = new Event()
			event.setPosition(position)
			event.setType(EventType.CALENDAR)
			event
		}
	}

	private boolean spansDay(Interval interval) {
		LocalDateTime startDateTime = interval.start.toLocalDate().atStartOfDay()
		LocalDateTime endDateTime = interval.end.toLocalDate().atStartOfDay()

		endDateTime.isAfter(startDateTime)
	}

	private List<LocalDateTime> getStartOfDaysBetweenInterval(Interval interval) {
		LocalDateTime startDateTime = interval.start.toLocalDate().atStartOfDay()
		LocalDateTime endDateTime = interval.end.toLocalDate().atStartOfDay()

		List<LocalDateTime> startTimes = []
		while (endDateTime.isAfter(startDateTime)) {
			startDateTime = startDateTime.plusDays(1)
			startTimes << startDateTime
		}
		startTimes
	}

}

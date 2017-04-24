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
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.core.timeline.IdleTimeBandModel

import java.time.LocalDate
import java.time.LocalDateTime

class CalendarEventGenerator {

	List<Event> generateCalendarEvents(List<Interval> intervalList) {
		List<Activity> activityList = getSortedDaysWithActivity(intervalList)
		if (activityList.size() > 0) {
			while(activityList.last().isIdle()) {
				activityList.remove(activityList.size() -1)
			}
		}

		Set<LocalDate> activityDaySet = []
		LocalDate lastActiveDay = null
		for (Activity activity : activityList) {
			if (lastActiveDay != null) {
				if (lastActiveDay.isBefore(activity.localDate)) {
					if (activity.idle) {
						activityDaySet << activity.localDate
					} else {
						LocalDate activityDay = activity.localDate

						while (lastActiveDay.isBefore(activityDay)) {
							activityDaySet << activityDay
							activityDay = activityDay.minusDays(1)
						}
					}
				}
			}

			lastActiveDay = activity.localDate
		}

		activityDaySet.sort().collect {
			createCalendarEvent(it.atStartOfDay())
		}
	}

	private Event createCalendarEvent(LocalDateTime position) {
		Event event = new Event()
		event.setPosition(position)
		event.setType(EventType.CALENDAR)
		event.setFullPath("/calendar/" + position.toLocalDate().toString().trim())
		event
	}

	private List<Activity> getSortedDaysWithActivity(List<Interval> intervals) {
		List<Activity> activityList = []
		for (Interval interval : intervals) {
			if (interval instanceof IdleTimeBandModel) {
				activityList << new Activity(interval.end, true)
			} else {
				activityList << new Activity(interval.start, false)
				activityList << new Activity(interval.end, false)
			}
		}

		activityList.sort(PositionableComparator.INSTANCE)
		activityList
	}


	private static final class Activity implements Positionable {

		private LocalDateTime position
		LocalDate localDate
		boolean idle

		Activity(LocalDateTime position, boolean idle) {
			this.position = position
			this.localDate = position.toLocalDate()
			this.idle = idle
		}

		@Override
		LocalDateTime getPosition() {
			return position
		}

		String toString() {
			"position=${localDate}, idle=${idle}"
		}

	}

}

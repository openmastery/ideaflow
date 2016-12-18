package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.Interval
import org.openmastery.publisher.api.IntervalComparator
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.core.timeline.IdleTimeBandModel

class CalendarEventGenerator {

	private IntervalGapGenerator intervalGapGenerator = new IntervalGapGenerator()

	List<Event> generateCalendarEvents(List<Interval> intervalList) {
		intervalList = sortIntervalsAndInsertIdleBandsBetweenIntervalGaps(intervalList)

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

	private List<Interval> sortIntervalsAndInsertIdleBandsBetweenIntervalGaps(List<Interval> intervalList) {
		List<Interval> gapBands = intervalGapGenerator.generateIntervalGapsAsIdleTimeBands(intervalList)

		List intervalsWithGaps = new ArrayList(intervalList)
		intervalsWithGaps.addAll(gapBands)
		intervalsWithGaps.sort(IntervalComparator.INSTANCE)
		intervalsWithGaps
	}

	private boolean spansDay(Interval interval) {
		DateTime startDateTime = interval.start.toDateTime().withTimeAtStartOfDay()
		DateTime endDateTime = interval.end.toDateTime().withTimeAtStartOfDay()

		endDateTime.isAfter(startDateTime)
	}

	private List<LocalDateTime> getStartOfDaysBetweenInterval(Interval interval) {
		DateTime startDateTime = interval.start.toDateTime().withTimeAtStartOfDay()
		DateTime endDateTime = interval.end.toDateTime().withTimeAtStartOfDay()

		List<LocalDateTime> startTimes = []
		while (endDateTime.isAfter(startDateTime)) {
			startDateTime = startDateTime.plusDays(1)
			startTimes << startDateTime.toLocalDateTime()
		}
		startTimes
	}

}

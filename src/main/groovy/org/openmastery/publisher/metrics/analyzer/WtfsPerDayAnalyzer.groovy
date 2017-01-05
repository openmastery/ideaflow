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
package org.openmastery.publisher.metrics.analyzer

import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.GraphPoint
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.MetricThreshold
import org.openmastery.time.TimeConverter

class WtfsPerDayAnalyzer extends AbstractTimelineAnalyzer<Double> {

	WtfsPerDayAnalyzer() {
		super(MetricType.WTFS_PER_DAY)
	}

	@Override
	List<GraphPoint<Double>> analyzeTimelineAndJourneys(IdeaFlowTimeline timeline, List<TroubleshootingJourney> journeys) {
		//journeys can fall in a day, but it seems like these data points would be daily or timeline focused and ignore journeys
		//we can generate daily points, then a maximum of all the days.

		List<Event> wtfEvents = timeline.events.findAll() { Event event ->
			event.type == EventType.WTF
		}

		LocalDate start = timeline.start.toLocalDate()
		LocalDate end = timeline.end.toLocalDate()

		List<GraphPoint<Double>> allPoints = calculateWtfsPerDay(start, end, wtfEvents)

		GraphPoint timelinePoint = createTimelinePoint(timeline, journeys)
		timelinePoint.frequency = allPoints.size()
		timelinePoint.value = getMaximumValue(allPoints)
		timelinePoint.danger = isOverThreshold(timelinePoint.value)

		allPoints.add(timelinePoint)

		return allPoints
	}

	private List<GraphPoint<Double>> calculateWtfsPerDay(LocalDate start, LocalDate end, List<Event> wtfEvents) {
		List<GraphPoint<Double>> allPoints = []

		for (LocalDate currentdate = start; currentdate.isBefore(end) || currentdate.isEqual(end);
			 currentdate = currentdate.plusDays(1)) {

			int dailyWtfCount = countWtfsForTheDay(wtfEvents, currentdate)

			if (dailyWtfCount > 0) {
				String formattedDate = TimeConverter.formatDate(currentdate)
				GraphPoint<Double> dailyWtfsPoint = createEmptyPoint("/daily/" + formattedDate)
				dailyWtfsPoint.position = currentdate.toLocalDateTime(new LocalTime(0))
				dailyWtfsPoint.relativePositionInSeconds = findFirstRelativePositionWithMatchingDate(wtfEvents, currentdate)
				dailyWtfsPoint.value = dailyWtfCount
				dailyWtfsPoint.danger = isOverThreshold(dailyWtfsPoint.value)
				allPoints.add(dailyWtfsPoint)
			}
		}

		return allPoints
	}

	long findFirstRelativePositionWithMatchingDate(List<Event> wtfEvents, LocalDate localDate) {
		Event event = wtfEvents.find() { Event wtf ->
			wtf.position.toLocalDate().equals(localDate)
		}
		event.relativePositionInSeconds
	}

	int countWtfsForTheDay(List<Event> wtfEvents, LocalDate currentDate) {
		List<Event> wtfsForTheDay = wtfEvents.findAll() { Event wtf ->
			wtf.position.toLocalDate().equals(currentDate)
		}
		return wtfsForTheDay.size()
	}

	@Override
	MetricThreshold<Double> getDangerThreshold() {
		return createMetricThreshold(10D)
	}
}

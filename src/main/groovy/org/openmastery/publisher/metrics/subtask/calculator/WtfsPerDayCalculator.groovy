/*
 * Copyright 2016 New Iron Group, Inc.
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
package org.openmastery.publisher.metrics.subtask.calculator

import org.joda.time.LocalDate
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType

class WtfsPerDayCalculator extends AbstractMetricsCalculator<Double> {

	WtfsPerDayCalculator() {
		super(MetricType.WTFS_PER_DAY)
	}

	@Override
	Metric<Double> calculateMetrics(IdeaFlowTimeline timeline) {
		Metric<Double> metric = createMetric()

		List<Event> wtfEvents = timeline.events.findAll() { Event event ->
			event.type == EventType.WTF
		}

		LocalDate start = timeline.start.toLocalDate();
		LocalDate end = timeline.end.toLocalDate()

		metric.value = calculateAverageWtfsPerDay(start, end, wtfEvents)

		return metric
	}

	private double calculateAverageWtfsPerDay(LocalDate start, LocalDate end, List<Event> wtfEvents) {
		double avgWtfsPerDay = 0
		double numberSamples = 0

		for (LocalDate currentdate = start; currentdate.isBefore(end) || currentdate.isEqual(end);
			 currentdate = currentdate.plusDays(1)) {

			int dailyWtfCount = countWtfsForTheDay(wtfEvents, currentdate)
			numberSamples++

			avgWtfsPerDay = ((avgWtfsPerDay * (numberSamples - 1)) + dailyWtfCount) / numberSamples
		}

		return avgWtfsPerDay
	}

	int countWtfsForTheDay(List<Event> wtfEvents, LocalDate currentDate) {
		List<Event> wtfsForTheDay = wtfEvents.findAll() { Event wtf ->
			wtf.position.toLocalDate().equals(currentDate)
		}
		return wtfsForTheDay.size()
	}
}

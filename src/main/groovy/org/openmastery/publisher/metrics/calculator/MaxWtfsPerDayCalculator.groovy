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
package org.openmastery.publisher.metrics.calculator

import org.joda.time.LocalDate
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.storyweb.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.metrics.MetricThreshold

class MaxWtfsPerDayCalculator extends AbstractMetricsCalculator<Double> {

	MaxWtfsPerDayCalculator() {
		super(MetricType.WTFS_PER_DAY)
	}

	@Override
	Metric<Double> calculateMetrics(IdeaFlowTimeline timeline) {

		List<Event> wtfEvents = timeline.events.findAll() { Event event ->
			event.type == EventType.WTF
		}

		LocalDate start = timeline.start.toLocalDate();
		LocalDate end = timeline.end.toLocalDate()

		Metric<Double> metric = createMetric()
		metric.value = calculateMaxWtfsPerDay(start, end, wtfEvents)
		metric.danger = metric.value > getDangerThreshold().threshold
		return metric
	}

	@Override
	MetricThreshold<Double> getDangerThreshold() {
		return createMetricThreshold(10D)
	}


	private double calculateMaxWtfsPerDay(LocalDate start, LocalDate end, List<Event> wtfEvents) {
		Double maxCount = 0;

		for (LocalDate currentdate = start; currentdate.isBefore(end) || currentdate.isEqual(end);
			 currentdate = currentdate.plusDays(1)) {

			int dailyWtfCount = countWtfsForTheDay(wtfEvents, currentdate)
			if (dailyWtfCount > maxCount) {
				maxCount = dailyWtfCount
			}
		}

		return maxCount
	}

	int countWtfsForTheDay(List<Event> wtfEvents, LocalDate currentDate) {
		List<Event> wtfsForTheDay = wtfEvents.findAll() { Event wtf ->
			wtf.position.toLocalDate().equals(currentDate)
		}
		return wtfsForTheDay.size()
	}
}

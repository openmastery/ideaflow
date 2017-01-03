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
package org.openmastery.publisher.metrics.subtask

import org.openmastery.publisher.api.RelativeInterval
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.CapacityDistribution
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.api.metrics.MetricsCalculator
import org.openmastery.publisher.api.metrics.SubtaskOverview
import org.openmastery.publisher.metrics.subtask.calculator.*
import org.openmastery.storyweb.api.MetricThreshold
import org.springframework.stereotype.Component

@Component
public class MetricsService {


	public List<Metric> generateJourneyMetrics(IdeaFlowTimeline timelineSegment) {
		MetricSetCalculator metricSet = new MetricSetCalculator()
		addMetric(metricSet, new AvgFeedbackLoopsCalculator())
		addMetric(metricSet, new AvgFeedbackLoopDurationCalculator())

		metricSet.calculate(timelineSegment)

		return metricSet.allMetrics
	}

	public SubtaskOverview generateSubtaskOverview(Event subtask, IdeaFlowTimeline timelineSegment) {

		SubtaskOverview overview = new SubtaskOverview()
		overview.subtaskEvent = subtask
		overview.durationInSeconds = timelineSegment.durationInSeconds

		overview.capacityDistribution = calculateCapacityDistribution(timelineSegment)

		MetricSetCalculator metricSet = generateDefaultMetricSet()

		metricSet.calculate(timelineSegment)

		overview.metrics = metricSet.allMetrics

		return overview
	}

	public CapacityDistribution calculateCapacityDistribution(IdeaFlowTimeline timeline) {
		CapacityDistributionCalculator calculator = new CapacityDistributionCalculator();
		return calculator.calculateCapacityDistribution(timeline)
	}

	public CapacityDistribution calculateCapacityDistribution(IdeaFlowTimeline timeline, RelativeInterval interval) {

		CapacityDistributionCalculator calculator = new CapacityDistributionCalculator();
		return calculator.calculateCapacityWithinWindow(timeline, interval.relativeStart, interval.relativeEnd)
	}

	MetricSetCalculator generateDefaultMetricSet() {
		MetricSetCalculator metricSet = new MetricSetCalculator()
		addMetric(metricSet, new WtfsPerDayCalculator())
		addMetric(metricSet, new MaxHaystackSizeCalculator())
		addMetric(metricSet, new MaxWtfDurationCalculator())
		addMetric(metricSet, new AvgFeedbackLoopsCalculator())
		addMetric(metricSet, new AvgFeedbackLoopDurationCalculator())
		return metricSet
	}

	List<MetricThreshold<?>> getDefaultMetricsThresholds() {
		MetricSetCalculator metricSet = generateDefaultMetricSet()
		metricSet.getAllMetricThresholds()
	}

	private static class MetricSetCalculator {

		Map<MetricType, MetricsCalculator> calculators = [:];
		List<MetricThreshold<?>> thresholds = [];

		List<Metric<?>> allMetrics = []

		public void addMetric(MetricType type, MetricsCalculator calculator) {

			calculators.put(type, calculator);
			thresholds.add(calculator.getDangerThreshold())
		}

		public void calculate(IdeaFlowTimeline timeline) {

			for (MetricsCalculator calculator : calculators.values()) {
				Metric<?> result = calculator.calculateMetrics(timeline);
				allMetrics.add(result);
			}
		}

		List<MetricThreshold<?>> getAllMetricThresholds() {
			thresholds
		}
	}

	void addMetric(MetricSetCalculator metricSet, MetricsCalculator calculator) {
		MetricType metricType = calculator.getMetricType()
		metricSet.addMetric(metricType, calculator)
	}


}

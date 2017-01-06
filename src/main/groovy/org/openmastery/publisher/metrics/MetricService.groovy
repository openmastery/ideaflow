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
package org.openmastery.publisher.metrics

import org.openmastery.publisher.api.RelativeInterval
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.*
import org.openmastery.publisher.metrics.calculator.*
import org.openmastery.storyweb.api.metrics.MetricThreshold
import org.springframework.stereotype.Component

@Component
public class MetricService {

	public List<Metric> generateJourneyMetrics(IdeaFlowTimeline timelineSegment) {
		MetricSetCalculator metricSet = new MetricSetCalculator()
		addMetric(metricSet, new MaxExperimentCycleCountCalculator())
		addMetric(metricSet, new MaxHumanCycleRatioCalculator())
		addMetric(metricSet, new MaxWtfsPerDayCalculator())
		addMetric(metricSet, new MaxResolutionTimeCalculator())
		metricSet.calculate(timelineSegment)

		return metricSet.allMetrics
	}

	public List<Metric> generateJourneySetMetrics(IdeaFlowTimeline timelineSegment) {
		MetricSetCalculator metricSet = new MetricSetCalculator()
		addMetric(metricSet, new MaxExperimentCycleCountCalculator())
		addMetric(metricSet, new MaxHumanCycleRatioCalculator())
		addMetric(metricSet, new MaxWtfsPerDayCalculator())
		addMetric(metricSet, new MaxResolutionTimeCalculator())
		metricSet.calculate(timelineSegment)

		return metricSet.allMetrics
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
		addMetric(metricSet, new MaxWtfsPerDayCalculator())
		addMetric(metricSet, new MaxHaystackSizeCalculator())
		addMetric(metricSet, new MaxResolutionTimeCalculator())
		addMetric(metricSet, new MaxExperimentCycleCountCalculator())
		addMetric(metricSet, new MaxHumanCycleRatioCalculator())
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

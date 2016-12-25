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
package org.openmastery.publisher.metrics.subtask

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.ideaflow.IdeaFlowMetricsTimeline
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.api.metrics.MetricsCalculator
import org.openmastery.publisher.api.metrics.TimelineMetrics
import org.openmastery.publisher.metrics.subtask.calculator.AvgFeedbackLoopDurationCalculator
import org.openmastery.publisher.metrics.subtask.calculator.AvgFeedbackLoopsCalculator
import org.openmastery.publisher.metrics.subtask.calculator.CapacityDistributionCalculator
import org.openmastery.publisher.metrics.subtask.calculator.MaxHaystackSizeCalculator
import org.openmastery.publisher.metrics.subtask.calculator.MaxWtfDurationCalculator
import org.openmastery.publisher.metrics.subtask.calculator.WtfsPerDayCalculator

public class RiskSummaryCalculator {

	TimelineMetrics calculateSubtaskMetrics(Event subtask, IdeaFlowMetricsTimeline timelineSegment) {

		TimelineMetrics metrics = new TimelineMetrics()
		metrics.id = subtask.id
		metrics.description = subtask.comment
		metrics.durationInSeconds = timelineSegment.durationInSeconds

		addMetric(metrics, new WtfsPerDayCalculator())
		addMetric(metrics, new MaxHaystackSizeCalculator())
		addMetric(metrics, new MaxWtfDurationCalculator())
		addMetric(metrics, new AvgFeedbackLoopsCalculator())
		addMetric(metrics, new AvgFeedbackLoopDurationCalculator())
		addMetric(metrics, new CapacityDistributionCalculator())

		metrics.calculate(timelineSegment)

		return metrics
	}

	void addMetric(TimelineMetrics metrics, MetricsCalculator calculator) {
		MetricType metricType = calculator.getMetricType()
		metrics.addMetric(metricType, calculator)
	}


}

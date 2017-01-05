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

import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.MeasurableContext
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.GraphPoint
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.MetricThreshold

abstract class AbstractTimelineAnalyzer<T extends Comparable<T>> {

	private MetricType metricType;

	AbstractTimelineAnalyzer(MetricType metricType) {
		this.metricType = metricType
	}


	abstract GraphPoint<T> analyzeTimelineAndJourneys(IdeaFlowTimeline timeline, List<TroubleshootingJourney> journeys);

	abstract MetricThreshold<T> getDangerThreshold();

	public MetricType getMetricType() {
		return metricType;
	}

	protected Metric<T> createMetric() {
		Metric<T> metric = new Metric<T>()
		metric.type = getMetricType()
		metric.danger = false
		return metric;
	}

	protected MetricThreshold<T> createMetricThreshold(T value) {
		MetricThreshold<T> threshold = new MetricThreshold<>()
		threshold.metricType = getMetricType()
		threshold.threshold = value
		return threshold
	}

	GraphPoint<T> createPointFromMeasurableContext(String relativePath, MeasurableContext measurable) {
		GraphPoint<T> point = new GraphPoint<>()
		point.relativePath = relativePath + "/"+ measurable.id
		point.painTags = measurable.painTags
		point.position = measurable.position
		point.frequency = measurable.frequency
		point.relativePositionInSeconds = measurable.relativePositionInSeconds
		point.metricType = getMetricType()
		return point
	}

	GraphPoint<T> createEmptyPoint(String relativePath) {
		GraphPoint<T> point = new GraphPoint<T>()
		point.relativePath = relativePath
		point.metricType = getMetricType()
		point.frequency = 1
		return point
	}


	GraphPoint<T> createTimelinePoint(IdeaFlowTimeline timeline, List<TroubleshootingJourney> journeys) {
		GraphPoint<T> graphPoint = new GraphPoint<>()
		graphPoint.relativePath = "/timeline"
		graphPoint.frequency = journeys.size()
		graphPoint.metricType = getMetricType()
		graphPoint.position = timeline.start
		graphPoint.relativePositionInSeconds = timeline.relativePositionInSeconds
		return graphPoint
	}

	T getMaximumValue(Collection<GraphPoint<T>> graphPoints) {
		T maxValue = null;
		graphPoints.each { GraphPoint<T> point ->
			if (maxValue == null || point.value > maxValue)  {
				maxValue = point.value
			}
		}
		return maxValue
	}

	T getSumOfValues(Collection<GraphPoint<T>> graphPoints) {
		T sum = null;
		graphPoints.each { GraphPoint<T> point ->
			if (sum == null) {
				sum = point.value
			} else {
				sum = sum + point.value
			}
		}
		return sum
	}

	T getWeightedAverage(Collection<GraphPoint<T>> graphPoints) {
		T sum = null
		int totalSamples = 0;

		graphPoints.each { GraphPoint<T> point ->
			if (sum == null) {
				sum = point.value * point.frequency
			} else {
				sum += point.value * point.frequency
			}
			totalSamples += point.frequency
		}

		T average = sum
		if (totalSamples > 0) {
			average = sum / totalSamples
		}
		return average
	}

	int getSumOfFrequency(Collection<GraphPoint<T>> graphPoints) {
		int frequency = 0;
		graphPoints.each { GraphPoint<T> point ->
			frequency += point.frequency
		}
		return frequency
	}

	boolean isOverThreshold(T value) {
		MetricThreshold<T> threshold = getDangerThreshold()
		return value > threshold.threshold
	}
}

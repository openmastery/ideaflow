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
package org.openmastery.storyweb.core.metrics.analyzer

import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.IdeaFlowStory
import org.openmastery.publisher.api.journey.StoryElement
import org.openmastery.publisher.api.journey.SubtaskStory
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.storyweb.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.metrics.GraphPoint
import org.openmastery.storyweb.api.metrics.MetricThreshold

abstract class AbstractTimelineAnalyzer<T extends Comparable<T>> {

	private MetricType metricType
	boolean useParentTimelineOnly

	AbstractTimelineAnalyzer(MetricType metricType, boolean useParentTimelineOnly) {
		this.metricType = metricType
		this.useParentTimelineOnly = useParentTimelineOnly
	}


	GraphPoint<T> analyzeIdeaFlowStory(IdeaFlowStory story) {

		List<GraphPoint<T>> subtaskPoints = [];

		GraphPoint<T> summaryPoint

		if (useParentTimelineOnly) {
			summaryPoint = analyzeIdeaFlowStory(story.timeline, collectAllJourneys(story))
			summaryPoint = assignBlameToSubtasks(story, summaryPoint)
		} else {
			story.subtasks.each { SubtaskStory subtaskStory ->

				List<TroubleshootingJourney> journeys = subtaskStory.troubleshootingJourneys
				GraphPoint<T> graphPoint = analyzeIdeaFlowStory(subtaskStory.timeline, journeys);
				if (graphPoint != null) {
					graphPoint.relativePath = subtaskStory.relativePath
					graphPoint.contextTags = subtaskStory.contextTags
					subtaskPoints.add(graphPoint)
				}
			}
			summaryPoint = createAggregatePoint(story.timeline, subtaskPoints)
		}

		if (summaryPoint != null) {
			summaryPoint.relativePath = story.relativePath
			summaryPoint.contextTags = story.contextTags
		}

		return summaryPoint
	}


	GraphPoint<T> assignBlameToSubtasks(IdeaFlowStory ideaFlowStory, GraphPoint<T> summaryPoint) {
		return summaryPoint
	}


	private List<TroubleshootingJourney> collectAllJourneys(IdeaFlowStory story) {
		List<TroubleshootingJourney> journeys = []
		story.subtasks.each { SubtaskStory subtaskStory ->
			journeys.addAll(subtaskStory.troubleshootingJourneys)
		}
		return journeys
	}


	abstract GraphPoint<T> analyzeIdeaFlowStory(IdeaFlowTimeline timeline, List<TroubleshootingJourney> journeys);

	abstract GraphPoint<T> createAggregatePoint(IdeaFlowTimeline timeline, List<GraphPoint<T>> graphPoints);


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

	GraphPoint<T> createPointFromStoryElement(StoryElement storyElement) {
		GraphPoint<T> point = new GraphPoint<>()
		point.relativePath = storyElement.relativePath
		point.painTags = storyElement.painTags
		point.position = storyElement.position
		point.distance = storyElement.durationInSeconds
		point.frequency = Math.max(storyElement.frequency, 1)
		point.relativePositionInSeconds = storyElement.relativePositionInSeconds
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


	GraphPoint<T> createTimelinePoint(IdeaFlowTimeline timeline, List<GraphPoint<T>> allPoints) {
		GraphPoint<T> graphPoint = new GraphPoint<>()
		graphPoint.relativePath = "/timeline"
		graphPoint.position = timeline.start
		graphPoint.distance = timeline.durationInSeconds
		graphPoint.relativePositionInSeconds = timeline.relativePositionInSeconds
		graphPoint.metricType = getMetricType()
		graphPoint.frequency = Math.max(allPoints.size(), 1)
		graphPoint.childPoints = allPoints
		return graphPoint
	}

	abstract T createEmptyValue();

	T getMaximumValue(Collection<GraphPoint<T>> graphPoints) { //get max of empty = null
		T maxValue = null;
		graphPoints.each { GraphPoint<T> point ->
			if (maxValue == null || point.value > maxValue)  {
				maxValue = point.value
			}
		}
		if (maxValue == null) {
			maxValue = createEmptyValue()
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
		if (sum == null) {
			sum = createEmptyValue()
		}
		return sum
	}

	T getWeightedAverage(Collection<GraphPoint<T>> graphPoints) {
		T sum = null
		int totalSamples = 0;

		graphPoints.each { GraphPoint<T> point ->
			if (sum == null) {
				//TODO fix NPE happening on this line
				sum = point.value * point.frequency
			} else {
				sum += point.value * point.frequency
			}
			totalSamples += point.frequency
		}
		if (sum == null) {
			sum = createEmptyValue()
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
		frequency = Math.max(1, frequency)
		return frequency
	}

	boolean isOverThreshold(T value) {
		MetricThreshold<T> threshold = getDangerThreshold()
		return value > threshold.threshold
	}

	
}

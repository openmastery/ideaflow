package org.openmastery.publisher.metrics.analyzer

import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.Measurable
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.metrics.GraphPoint
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.MetricThreshold

abstract class AbstractTimelineAnalyzer<T> {

	private MetricType metricType;

	AbstractTimelineAnalyzer(MetricType metricType) {
		this.metricType = metricType
	}

	abstract List<GraphPoint<T>> analyzeTimelineAndJourneys(IdeaFlowTimeline timeline, List<TroubleshootingJourney> journeys);

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

	GraphPoint<T> createPoint(String relativePath, Measurable measurable) {
		GraphPoint<T> point = new GraphPoint<>()
		point.relativePath = relativePath + "/"+ measurable.id
		point.painTags = measurable.painTags
		point.position = measurable.position
		point.frequency = measurable.frequency
		point.relativePositionInSeconds = measurable.relativePositionInSeconds
		point.metricType = getMetricType()
		return point
	}
}

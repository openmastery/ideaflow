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

import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.api.metrics.MetricsCalculator
import org.openmastery.storyweb.api.metrics.MetricThreshold

abstract class AbstractMetricsCalculator<T> implements MetricsCalculator<T> {

	private MetricType metricType;

	AbstractMetricsCalculator(MetricType metricType) {
		this.metricType = metricType
	}

	@Override
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

}
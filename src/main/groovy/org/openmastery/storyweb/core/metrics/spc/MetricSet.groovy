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
package org.openmastery.storyweb.core.metrics.spc

import org.openmastery.publisher.api.journey.IdeaFlowStory
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.metrics.GraphPoint
import org.openmastery.storyweb.api.metrics.Metric
import org.openmastery.storyweb.api.metrics.MetricThreshold
import org.openmastery.storyweb.core.metrics.analyzer.AbstractTimelineAnalyzer

class MetricSet {

	Map<MetricType, AbstractTimelineAnalyzer> calculators = [:];

	List<GraphPoint<?>> explodableTrees = []
	List<Metric<?>> flattenedMetrics = []
	List<MetricThreshold<?>> painThresholds = []

	void addMetric(AbstractTimelineAnalyzer calculator) {
		calculators.put(calculator.getMetricType(), calculator);
		painThresholds.add(calculator.getDangerThreshold());
	}

	MetricSet calculate(IdeaFlowStory story) {
		for (AbstractTimelineAnalyzer calculator : calculators.values()) {
			GraphPoint<?> tree = calculator.analyzeIdeaFlowStory(story);
			if (tree) {
				tree.forcePushContextTagsToChildren()
				tree.forceBubbleUpAllPain()

				explodableTrees.add(tree);
			}
		}
		explodableTrees.each { println "Top of tree:" + it.relativePath + ":" + it.metricType }
		flattenedMetrics = toFlattenedMetrics("", explodableTrees)

		return this
	}

	private List<Metric<?>> toFlattenedMetrics(String prefix, List<GraphPoint<?>> graphPoints) {
		List<Metric<?>> flatMetrics = []
		graphPoints.each { GraphPoint<?> point ->
			Metric<?> metric = point.toMetric(prefix)
			flatMetrics.add(metric)
			flatMetrics.addAll(toFlattenedMetrics(prefix + point.relativePath, point.childPoints))
		}
		return flatMetrics
	}
}
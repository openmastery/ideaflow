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
package org.openmastery.publisher.ideaflow.story

import org.openmastery.publisher.api.journey.PainCycle
import org.openmastery.publisher.api.journey.IdeaFlowStory
import org.openmastery.publisher.api.journey.SubtaskStory
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.storyweb.api.metrics.Metric
import org.openmastery.storyweb.core.MetricsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MetricsDecorator {

	@Autowired
	MetricsService metricsService

	void decorateStoryWithMetrics(IdeaFlowStory story) {

		Map<String, Metric<?>> metrics = metricsService.generateMetricsForTask(story).flattenedMetrics.collectEntries {
			[it.relativePath, it]
		}

		story.allMetrics = findAllMetricMatchingPath(story.fullPath, metrics)
		story.dangerMetrics = findAllDangerMetrics(story.allMetrics)

		story.subtasks.each { SubtaskStory subtaskStory ->
			subtaskStory.allMetrics = findAllMetricMatchingPath(subtaskStory.fullPath, metrics)
			subtaskStory.dangerMetrics = findAllDangerMetrics(subtaskStory.allMetrics)

			subtaskStory.troubleshootingJourneys.each { TroubleshootingJourney journey ->
				journey.allMetrics = findAllMetricMatchingPath(journey.fullPath, metrics)
				journey.dangerMetrics = findAllDangerMetrics(journey.allMetrics)

				journey.painCycles.each { PainCycle cycle ->
					cycle.allMetrics = findAllMetricMatchingPath(cycle.fullPath, metrics)
					cycle.dangerMetrics = findAllDangerMetrics(cycle.allMetrics)
				}
			}
		}

	}

	List<Metric<?>> findAllDangerMetrics(List<Metric<?>> metrics) {
		metrics.findAll { Metric<?> metric ->
			metric.danger == true
		}
	}

	List<Metric<?>> findAllMetricMatchingPath(String storyPath, Map<String, Metric<?>> metrics) {
		metrics.findAll { String metricPath, Metric<?> metric ->
			matchesPath(storyPath, metricPath)
		}.values().toList()
	}

	boolean matchesPath(String searchPath, String metricPath) {
		metricPath =~ "^${searchPath}/[^/]+\$"
	}
}

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

import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.MetricThreshold

class MaxResolutionTimeCalculator extends AbstractMetricsCalculator<DurationInSeconds> {

	MaxResolutionTimeCalculator() {
		super(MetricType.MAX_RESOLUTION_TIME)
	}


	@Override
	Metric<DurationInSeconds> calculateMetrics(IdeaFlowTimeline timeline) {

		List<IdeaFlowBand> troubleshootingBands = timeline.ideaFlowBands.findAll { IdeaFlowBand band ->
			band.type == IdeaFlowStateType.TROUBLESHOOTING
		}

		Long maxDuration = 0
		troubleshootingBands.each { IdeaFlowBand troubleshootingBand ->
			if (troubleshootingBand.durationInSeconds > maxDuration) {
				maxDuration = troubleshootingBand.durationInSeconds
			}
		}

		Metric<DurationInSeconds> metric = createMetric()
		metric.type = getMetricType()
		metric.value = new DurationInSeconds(maxDuration)
		metric.danger = metric.value.greaterThan(getDangerThreshold().threshold)

		return metric
	}


	@Override
	MetricThreshold<DurationInSeconds> getDangerThreshold() {
		return createMetricThreshold(new DurationInSeconds(30 * 60))
	}

}

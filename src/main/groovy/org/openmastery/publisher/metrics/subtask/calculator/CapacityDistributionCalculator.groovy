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
package org.openmastery.publisher.metrics.subtask.calculator

import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.CapacityDistribution
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType

class CapacityDistributionCalculator extends AbstractMetricsCalculator<CapacityDistribution> {

	CapacityDistributionCalculator() {
		super(MetricType.CAPACITY_DISTRIBUTION)
	}


	@Override
	Metric<CapacityDistribution> calculateMetrics(IdeaFlowTimeline timeline) {

		CapacityDistribution capacity = new CapacityDistribution()

		saveTotalDurationForBandType(capacity, timeline, IdeaFlowStateType.LEARNING)
		saveTotalDurationForBandType(capacity, timeline, IdeaFlowStateType.PROGRESS)
		saveTotalDurationForBandType(capacity, timeline, IdeaFlowStateType.TROUBLESHOOTING)

		Metric<CapacityDistribution> metric = createMetric()
		metric.type = getMetricType()
		metric.value = capacity

		return metric
	}

	@Override
	CapacityDistribution getDangerThreshold() {
		return null
	}

	void saveTotalDurationForBandType(CapacityDistribution capacity, IdeaFlowTimeline timeline, IdeaFlowStateType ideaFlowStateType) {

		List<IdeaFlowBand> bands = timeline.ideaFlowBands.findAll() { IdeaFlowBand band ->
			band.type == ideaFlowStateType
		}

		long totalDuration = 0

		bands.each { IdeaFlowBand band ->
			totalDuration += band.durationInSeconds
		}

		capacity.addTotalDurationForType(ideaFlowStateType, totalDuration)

	}
}

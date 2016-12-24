package org.openmastery.publisher.metrics.subtask.calculator

import org.joda.time.Duration
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.CapacityDistribution
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.metrics.subtask.MetricsCalculator


class CapacityDistributionCalculator implements MetricsCalculator<CapacityDistribution> {


	@Override
	Metric<CapacityDistribution> calculateMetrics(IdeaFlowTimeline timeline) {

		CapacityDistribution capacity = new CapacityDistribution()

		saveTotalDurationForBandType(capacity, timeline, IdeaFlowStateType.LEARNING)
		saveTotalDurationForBandType(capacity, timeline, IdeaFlowStateType.PROGRESS)
		saveTotalDurationForBandType(capacity, timeline, IdeaFlowStateType.TROUBLESHOOTING)

		Metric<CapacityDistribution> metric = new Metric<CapacityDistribution>()
		metric.type = MetricType.CAPACITY_DISTRIBUTION
		metric.value = capacity
		return metric
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

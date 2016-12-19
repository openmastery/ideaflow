package org.openmastery.publisher.metrics.calculator

import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.CalculatorSpecification
import org.openmastery.publisher.api.metrics.GroupBy
import org.openmastery.publisher.api.metrics.IdeaFlowMetrics
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType


public class RiskSummaryBySubtaskCalculator implements MetricsCalculator {

	@Override
	IdeaFlowMetrics calculateMetrics(IdeaFlowTimeline timeline) {
		//slice timeline by subtask
		//for each timeline slice, generate a list of named metrics

		//max batch size, static metrics.  I've just got a list of static stuff.
		//each value is essentially a graphable point

		//SubtaskId, Metric

		Map<String, List<Metric>> metricsBySubtaskMap = [:]
		metricsBySubtaskMap.put("subtaskName", [])

		return IdeaFlowMetrics.builder()
			.groupType(GroupBy.SUB_TASK)
			.metricResults(metricsBySubtaskMap)
			.build()

	}
}

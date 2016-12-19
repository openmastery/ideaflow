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

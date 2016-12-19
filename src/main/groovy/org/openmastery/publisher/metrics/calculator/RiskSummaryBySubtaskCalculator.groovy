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

import org.joda.time.Duration
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.publisher.api.metrics.SubtaskMetrics

public class RiskSummaryBySubtaskCalculator implements SubtaskMetricsCalculator {

	@Override
	List<SubtaskMetrics> calculateSubtaskMetrics(IdeaFlowTimeline timeline) {

		List<Event> subtasks = timeline.getEvents().findAll { Event event ->
			event.type == EventType.SUBTASK
		}

		List<SubtaskMetrics> subtaskMetrics = []

		subtasks.each { Event subtask ->
			SubtaskMetrics metrics = new SubtaskMetrics()
			metrics.id = subtask.id
			metrics.description = subtask.comment
			metrics.durationInSeconds = 14546L

			metrics.addMetric(MetricType.IDEAFLOW_STRATEGY, 0.30)
			metrics.addMetric(MetricType.IDEAFLOW_PROGRESS, 0.50)
			metrics.addMetric(MetricType.IDEAFLOW_TROUBLESHOOTING, 0.20)

			metrics.addMetric(MetricType.WTFS_PER_DAY, 3.2)
			metrics.addMetric(MetricType.MAX_BATCH_SIZE, Duration.standardMinutes(15))
			metrics.addMetric(MetricType.MAX_WTF_DURATION, Duration.standardMinutes(12))
			metrics.addMetric(MetricType.AVG_FEEDBACK_LOOPS, 5.6)
			metrics.addMetric(MetricType.AVG_FEEDBACK_LOOP_DURATION, Duration.standardMinutes(12))

			subtaskMetrics.add(metrics)
		}
		//slice timeline by subtask
		//for each timeline slice, generate a list of named metrics

		//max batch size, static metrics.  I've just got a list of static stuff.
		//each value is essentially a graphable point

		//SubtaskId, Metric

		return subtaskMetrics
	}
}

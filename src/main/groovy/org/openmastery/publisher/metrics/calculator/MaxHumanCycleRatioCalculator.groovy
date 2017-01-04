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

import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.MetricThreshold

class MaxHumanCycleRatioCalculator extends AbstractMetricsCalculator<DurationInSeconds> {

	MaxHumanCycleRatioCalculator() {
		super(MetricType.MAX_HUMAN_CYCLE_RATIO)
	}

	/**
	 *
	 * What's the ratio of troubleshooting time to execution events within a troubleshooting band?
	 * What's the maximum ratio across all troubleshooting bands?
	 *
	 * @param timeline for a subtask
	 * @return Metric<Double> the resulting metric value
	 */
	@Override
	Metric<DurationInSeconds> calculateMetrics(IdeaFlowTimeline timeline) {

		List<IdeaFlowBand> troubleshootingBands = timeline.ideaFlowBands.findAll() { IdeaFlowBand band ->
			band.type == IdeaFlowStateType.TROUBLESHOOTING
		}

		Double maxRatio = 0;

		troubleshootingBands.each { IdeaFlowBand band ->
			int eventCount = countExecutionEventsInRange(timeline.executionEvents, band.relativeStart, band.relativeEnd)

			if (eventCount > 0) {
				Double durationRatio = ((double) band.durationInSeconds) / eventCount
				if (durationRatio > maxRatio) {
					maxRatio = durationRatio
				}
			}
		}

		Metric<DurationInSeconds> metric = createMetric()
		metric.value = new DurationInSeconds((long)maxRatio);
		metric.danger = metric.value.greaterThan(getDangerThreshold().threshold)
		return metric
	}

	@Override
	MetricThreshold<DurationInSeconds> getDangerThreshold() {
		return createMetricThreshold(new DurationInSeconds(10 * 60))
	}

	int countExecutionEventsInRange(List<ExecutionEvent> executionEvents, Long relativeStart, Long relativeEnd ) {
		//println relativeStart + " : " + relativeEnd + " = " + (relativeEnd - relativeStart)
		List<ExecutionEvent> eventsWithinRange = executionEvents.findAll() { ExecutionEvent event ->
			event.relativePositionInSeconds > relativeStart &&
					event.relativePositionInSeconds < relativeEnd
		}
		//println "Result : "+eventsWithinRange.collect { it.relativePositionInSeconds }
		return eventsWithinRange.size()
	}


}

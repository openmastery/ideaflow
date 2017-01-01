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
package org.openmastery.publisher.metrics.subtask.calculator

import org.joda.time.Duration
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType

class AvgFeedbackLoopDurationCalculator  extends AbstractMetricsCalculator<DurationInSeconds> {

	AvgFeedbackLoopDurationCalculator() {
		super(MetricType.AVG_FEEDBACK_LOOP_DURATION)
	}

	/**
	 *
	 * What's the ratio of troubleshooting time to execution events within a troubleshooting band?
	 *
	 * @param timeline for a subtask
	 * @return Metric<Double> the resulting metric value
	 */
	@Override
	Metric<DurationInSeconds> calculateMetrics(IdeaFlowTimeline timeline) {

		List<IdeaFlowBand> troubleshootingBands = timeline.ideaFlowBands.findAll() { IdeaFlowBand band ->
			band.type == IdeaFlowStateType.TROUBLESHOOTING
		}

		Double avgDuration = 0;
		Long sampleCount = 0;

		troubleshootingBands.each { IdeaFlowBand band ->
			int eventCount = countExecutionEventsInRange(timeline.executionEvents, band.relativeStart, band.relativeEnd)

			if (eventCount > 0) {
				Double durationRatio = ((double) band.durationInSeconds) / eventCount
				sampleCount++
				avgDuration = (avgDuration *(sampleCount - 1) + durationRatio)/sampleCount
			}
		}

		Metric<DurationInSeconds> metric = createMetric()
		metric.value = new DurationInSeconds((long)avgDuration);
		metric.danger = metric.value.greaterThan(getDangerThreshold())
		return metric
	}

	@Override
	DurationInSeconds getDangerThreshold() {
		return new DurationInSeconds(10 * 60)
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

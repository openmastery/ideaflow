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

import org.joda.time.Duration
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowMetricsTimeline
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType

class AvgFeedbackLoopDurationCalculator  extends AbstractMetricsCalculator<Duration> {

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
	Metric<Duration> calculateMetrics(IdeaFlowMetricsTimeline timeline) {

		List<IdeaFlowBand> troubleshootingBands = timeline.ideaFlowBands.findAll() { IdeaFlowBand band ->
			band.type == IdeaFlowStateType.TROUBLESHOOTING
		}

		Double avgDuration = 0;
		Long sampleCount = 0;

		troubleshootingBands.each { IdeaFlowBand troubleshootingBand ->
			Long relativeStart = troubleshootingBand.relativePositionInSeconds
			Long relativeEnd = troubleshootingBand.relativePositionInSeconds + troubleshootingBand.durationInSeconds

			int eventCount = countExecutionEventsInRange(timeline.executionEvents, relativeStart, relativeEnd)

			Double durationRatio = ((double) troubleshootingBand.durationInSeconds) / eventCount
			println durationRatio
			sampleCount++
			avgDuration = (avgDuration *(sampleCount - 1) + durationRatio)/sampleCount
			println avgDuration

		}

		Metric<Duration> metric = new Metric<Duration>()
		metric.type = getMetricType()
		metric.value = new Duration((long)(avgDuration * 1000))
		return metric
	}

	int countExecutionEventsInRange(List<ExecutionEvent> executionEvents, Long relativeStart, Long relativeEnd ) {
		println relativeStart + " : " + relativeEnd + " = " + (relativeEnd - relativeStart)
		List<ExecutionEvent> eventsWithinRange = executionEvents.findAll() { ExecutionEvent event ->
			event.relativePositionInSeconds > relativeStart &&
					event.relativePositionInSeconds < relativeEnd
		}
		println "Result : "+eventsWithinRange.collect { it.relativePositionInSeconds }
		return eventsWithinRange.size()
	}

	List<IdeaFlowBand> collapseConsecutiveBandPeriods(List<IdeaFlowBand> bands) {
		List<IdeaFlowBand> filteredBands = bands.findAll() { IdeaFlowBand band ->
			(band.type == IdeaFlowStateType.PROGRESS || band.type == IdeaFlowStateType.TROUBLESHOOTING)
		}

		filteredBands = filteredBands.sort { IdeaFlowBand band ->
			band.relativePositionInSeconds
		}

		List<IdeaFlowBand> consecutiveBandPeriods = []
		IdeaFlowBand lastBand = null
		filteredBands.each { IdeaFlowBand band ->
			if (lastBand == null) {
				lastBand = band
			} else {
				if (bandsAreAdjacent(lastBand, band)) {
					IdeaFlowBand newBand = IdeaFlowBand.builder()
							.relativePositionInSeconds(lastBand.relativePositionInSeconds)
							.durationInSeconds(lastBand.durationInSeconds + band.durationInSeconds)
							.build()
					lastBand = newBand
				} else {
					consecutiveBandPeriods.add(lastBand)
					lastBand = band
				}
			}
		}
		if (lastBand) consecutiveBandPeriods.add(lastBand)

		return consecutiveBandPeriods
	}

	boolean bandsAreAdjacent(IdeaFlowBand prevBand, IdeaFlowBand nextBand) {
		Long prevBandEnd = prevBand.relativePositionInSeconds + prevBand.durationInSeconds
		return (prevBandEnd == nextBand.relativePositionInSeconds)
	}


	List<Long> findRelativePositionsWithinRange(List<ExecutionEvent> allEvents, Long relativeStart, Long relativeEnd) {
		List<ExecutionEvent> eventsWithinBand = findEventsWithinRange(allEvents, relativeStart, relativeEnd)
		List<Long> relativeTimes = createRelativePositionsList(eventsWithinBand)
		relativeTimes.add(relativeStart)
		relativeTimes.add(relativeEnd)

		Collections.sort(relativeTimes)
		return relativeTimes
	}

	List<Long> createRelativePositionsList(List<ExecutionEvent> events) {
		List<Long> relativeTimes = events.collect { ExecutionEvent event ->
			event.relativePositionInSeconds
		}
		Collections.sort(relativeTimes)
		return relativeTimes
	}


	List<ExecutionEvent> findEventsWithinRange(List<ExecutionEvent> events, Long relativeStart, Long relativeEnd) {
		return events.findAll() { ExecutionEvent event ->
			event.relativePositionInSeconds > relativeStart && event.relativePositionInSeconds < relativeEnd
		}
	}
}

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
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType

/**
 * This metric is attempting to measure the maximum amount of time spent changing
 * code without validating that the code actually works.
 *
 * "Batch size" or "Haystack size" is measured in terms of time as opposed to
 * the volume of code change.  We look for the maximum amount of consecutive time
 * spent in "progress" without any execution events.
 */

class MaxHaystackSizeCalculator extends AbstractMetricsCalculator<Duration> {

	MaxHaystackSizeCalculator() {
		super(MetricType.MAX_HAYSTACK_SIZE)
	}


	@Override
	Metric<Duration> calculateMetrics(IdeaFlowTimeline timeline) {


		List<IdeaFlowBand> consecutiveBandPeriods = collapseConsecutiveBandPeriods(timeline.ideaFlowBands)
		println consecutiveBandPeriods
		Long maxDuration = 0

		consecutiveBandPeriods.each { IdeaFlowBand band ->
			Long relativeStart = band.relativePositionInSeconds
			Long relativeEnd = relativeStart + band.durationInSeconds

			List<Long> relativeEventTimes = findRelativePositionsWithinRange(timeline.executionEvents, relativeStart, relativeEnd)

			Long previousTime = null

			println relativeEventTimes
			relativeEventTimes.each { Long currentTime ->
				if (previousTime == null) {
					previousTime = currentTime
				}

				Long duration = currentTime - previousTime
				if (duration > maxDuration) {
					maxDuration = duration
				}
				previousTime = currentTime
			}
		}

		Metric<Duration> metric = new Metric<Duration>()
		metric.type = getMetricType()
		metric.value = Duration.standardSeconds(maxDuration)
		return metric
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

package org.openmastery.publisher.metrics.analyzer

import groovy.util.logging.Slf4j
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.metrics.GraphPoint
import org.openmastery.publisher.api.metrics.Metric
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.MetricThreshold

@Slf4j
class HaystackAnalyzer extends AbstractTimelineAnalyzer<DurationInSeconds> {

	HaystackAnalyzer() {
		super(MetricType.HAYSTACK_SIZE)
	}

	@Override
	List<GraphPoint<DurationInSeconds>> analyzeTimelineAndJourneys(IdeaFlowTimeline timeline, List<TroubleshootingJourney> journeys) {

		//exclude learning time, generally trailing troubleshooting time, but could be a whole string of haystacks...
		//maybe we get the haystack size

		Map<String, Long> maxHaystacksFound = [:]

		List<IdeaFlowBand> consecutiveBandPeriods = collapseConsecutiveBandPeriods(timeline.ideaFlowBands)
		log.debug("Collapsed bands:" + consecutiveBandPeriods )

		consecutiveBandPeriods.each { IdeaFlowBand band ->
			Long relativeStart = band.relativePositionInSeconds
			Long relativeEnd = relativeStart + band.durationInSeconds

			List<Long> relativeEventTimes = findRelativePositionsWithinRange(timeline.executionEvents, relativeStart, relativeEnd)

			Long previousTime = null

			relativeEventTimes.each { Long currentTime ->
				if (previousTime == null) {
					previousTime = currentTime
				}

				Long haystackSizeInSeconds = currentTime - previousTime
				log.debug("Haystack calculation [$previousTime to $currentTime] = haystack: $haystackSizeInSeconds")
				assignBlame(maxHaystacksFound, journeys, currentTime, haystackSizeInSeconds)
				previousTime = currentTime
			}
		}

		return translateToGraphPoints(timeline, journeys, maxHaystacksFound)

	}

	List<GraphPoint<DurationInSeconds>> translateToGraphPoints(IdeaFlowTimeline timeline, List<TroubleshootingJourney> journeys, Map<String, Long> maxHaystacksFound) {

		maxHaystacksFound.collect { String relativePath, Long haystackSizeInSeconds ->

			TroubleshootingJourney journey = findJourney(journeys, relativePath)
			GraphPoint<DurationInSeconds> graphPoint
			if (journey) {
				graphPoint = createPoint("/journey", journey)
			} else {
				graphPoint = new GraphPoint<>()
				graphPoint.relativePath = relativePath
				graphPoint.frequency = journeys.size()
				graphPoint.metricType = getMetricType()
				graphPoint.position = timeline.start
				graphPoint.relativePositionInSeconds = timeline.relativePositionInSeconds
			}
			graphPoint.value = new DurationInSeconds(haystackSizeInSeconds)
			graphPoint.danger = graphPoint.value.greaterThan(getDangerThreshold().threshold)
			return graphPoint
		}.sort { GraphPoint<DurationInSeconds> graphPoint ->
			graphPoint.relativePositionInSeconds
		}

	}

	TroubleshootingJourney findJourney(List<TroubleshootingJourney> journeys, String relativePath) {
		journeys.find { TroubleshootingJourney journey ->
			journey.relativePath == relativePath
		}
	}

	Map<String, Long> assignBlame(Map<String, Long> maxHaystacksFound, List<TroubleshootingJourney> journeys, long haystackEnd, long haystackSizeInSeconds) {

		for (TroubleshootingJourney journey : journeys) {
			if (haystackEnd <= journey.relativeEnd) {
				Long oldHaystack = maxHaystacksFound.get(journey.getRelativePath())
				if (oldHaystack == null || haystackSizeInSeconds > oldHaystack) {
					maxHaystacksFound.put(journey.relativePath, haystackSizeInSeconds)
				}
				break;
			}

			long haystackStart = haystackEnd - haystackSizeInSeconds
			if (haystackStart < journey.relativeEnd) {
				long partialHaystackSize = journey.relativeEnd - haystackStart
				adjustMaximumHaystack(maxHaystacksFound, journey, partialHaystackSize)
			}
		}
		Long oldHaystack = maxHaystacksFound.get("/timeline")
		if (oldHaystack == null || haystackSizeInSeconds > oldHaystack) {
			maxHaystacksFound.put("/timeline", haystackSizeInSeconds)
		}

		return maxHaystacksFound;
	}

	private void adjustMaximumHaystack(Map<String, Long> maxHaystacksFound, TroubleshootingJourney journey, long newHaystackSize) {
		Long oldHaystack = maxHaystacksFound.get(journey.getRelativePath())
		if (oldHaystack == null || newHaystackSize > oldHaystack) {
			maxHaystacksFound.put(journey.relativePath, newHaystackSize)
		}
	}

	@Override
	MetricThreshold<DurationInSeconds> getDangerThreshold() {
		return createMetricThreshold(new DurationInSeconds(60 * 60))
	}


	Metric<DurationInSeconds> calculateMetrics(IdeaFlowTimeline timeline) {


		List<IdeaFlowBand> consecutiveBandPeriods = collapseConsecutiveBandPeriods(timeline.ideaFlowBands)
		Long maxDuration = 0

		consecutiveBandPeriods.each { IdeaFlowBand band ->
			Long relativeStart = band.relativePositionInSeconds
			Long relativeEnd = relativeStart + band.durationInSeconds

			List<Long> relativeEventTimes = findRelativePositionsWithinRange(timeline.executionEvents, relativeStart, relativeEnd)

			Long previousTime = null

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

		Metric<DurationInSeconds> metric = createMetric()
		metric.type = getMetricType()
		metric.value = new DurationInSeconds(maxDuration)
		metric.danger = metric.value.greaterThan(getDangerThreshold().threshold)

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

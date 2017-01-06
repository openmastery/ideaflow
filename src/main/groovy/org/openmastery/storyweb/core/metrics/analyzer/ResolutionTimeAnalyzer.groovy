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
package org.openmastery.storyweb.core.metrics.analyzer

import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.DiscoveryCycle
import org.openmastery.publisher.api.journey.MeasurableContext
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.metrics.GraphPoint
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.metrics.MetricThreshold


class ResolutionTimeAnalyzer extends AbstractTimelineAnalyzer<DurationInSeconds> {

	ResolutionTimeAnalyzer() {
		super(MetricType.MAX_RESOLUTION_TIME)
	}

	@Override
	GraphPoint<DurationInSeconds> analyzeTimelineAndJourneys(IdeaFlowTimeline timeline, List<TroubleshootingJourney> journeys) {

		List<GraphPoint<DurationInSeconds>> allPoints = journeys.collect { TroubleshootingJourney journey ->
			GraphPoint<DurationInSeconds> bandPoint = createPointFromMeasurableContext("/journey", journey)
			bandPoint.childPoints = generatePointsForDiscoveryCycles(journey.discoveryCycles)
			return bandPoint
		}

		GraphPoint<DurationInSeconds> timelinePoint = createTimelinePoint(timeline, journeys)
		timelinePoint.value = getMaximumValue(allPoints)
		timelinePoint.danger = isOverThreshold(timelinePoint.value)
		timelinePoint.childPoints = allPoints
		return timelinePoint
	}

	List<GraphPoint<DurationInSeconds>> generatePointsForDiscoveryCycles(List<DiscoveryCycle> discoveryCycles) {

		return discoveryCycles.collect { DiscoveryCycle discoveryCycle ->
			return createPointFromMeasurableContext("/discovery", discoveryCycle)
		}
	}

	GraphPoint<DurationInSeconds> createPointFromMeasurableContext(String relativePath, MeasurableContext measurable) {
		GraphPoint<DurationInSeconds> point = super.createPointFromMeasurableContext(relativePath, measurable)
		point.value = new DurationInSeconds(measurable.getDurationInSeconds())
		point.danger = isOverThreshold(point.value)

		return point
	}

	@Override
	MetricThreshold<DurationInSeconds> getDangerThreshold() {
		return createMetricThreshold(new DurationInSeconds(30 * 60))
	}

}


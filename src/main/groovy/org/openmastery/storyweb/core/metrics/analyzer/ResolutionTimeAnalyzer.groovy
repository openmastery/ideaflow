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
import org.openmastery.publisher.api.journey.StoryElement
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.metrics.GraphPoint
import org.openmastery.storyweb.api.metrics.MetricThreshold

class ResolutionTimeAnalyzer extends AbstractTimelineAnalyzer<DurationInSeconds> {

	ResolutionTimeAnalyzer() {
		super(MetricType.MAX_RESOLUTION_TIME, false)
	}

	@Override
	GraphPoint<DurationInSeconds> analyzeIdeaFlowStory(IdeaFlowTimeline timeline, List<TroubleshootingJourney> journeys) {

		List<GraphPoint<DurationInSeconds>> allPoints = journeys.collect { TroubleshootingJourney journey ->
			GraphPoint<DurationInSeconds> bandPoint = createPointFromStoryElement(journey)
			bandPoint.childPoints = generatePointsForDiscoveryCycles(journey.discoveryCycles)
			return bandPoint
		}


		return createAggregatePoint(timeline, allPoints)
	}

	@Override
	GraphPoint<DurationInSeconds> createAggregatePoint(IdeaFlowTimeline timeline, List<GraphPoint<DurationInSeconds>> allPoints) {
		GraphPoint timelinePoint = null
		if (allPoints.size() > 0) {
			timelinePoint = createTimelinePoint(timeline, allPoints)
			timelinePoint.value = getMaximumValue(allPoints)
			timelinePoint.danger = isOverThreshold(timelinePoint.value)
		}

		return timelinePoint
	}

	List<GraphPoint<DurationInSeconds>> generatePointsForDiscoveryCycles(List<DiscoveryCycle> discoveryCycles) {
		List<GraphPoint<DurationInSeconds>> discoveryPoints =
				discoveryCycles.collect { DiscoveryCycle discoveryCycle ->
					 createPointFromStoryElement(discoveryCycle)
				}

		discoveryPoints.removeAll { GraphPoint<DurationInSeconds> discoveryPoint ->
			discoveryPoint.distance == 0L
		}
		return discoveryPoints
	}

	GraphPoint<DurationInSeconds> createPointFromStoryElement(StoryElement storyElement) {
		GraphPoint<DurationInSeconds> point = super.createPointFromStoryElement(storyElement)
		point.value = new DurationInSeconds(storyElement.getDurationInSeconds())
		point.danger = isOverThreshold(point.value)

		return point
	}

	@Override
	MetricThreshold<DurationInSeconds> getDangerThreshold() {
		return createMetricThreshold(new DurationInSeconds(30 * 60))
	}

}


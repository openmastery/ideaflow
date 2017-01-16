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

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.TagsUtil
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.metrics.GraphPoint
import org.openmastery.storyweb.api.metrics.MetricThreshold

class DisruptionsPerDayAnalyzer extends AbstractTimelineAnalyzer<Double> {

	DisruptionsPerDayAnalyzer() {
		super(MetricType.DISRUPTIONS_PER_DAY, false)
	}

	@Override
	GraphPoint<Double> analyzeIdeaFlowStory(IdeaFlowTimeline timeline, List<TroubleshootingJourney> journeys) {

		List<Event> disruptionEvents = MetricsUtils.findEventsMatchingType(timeline.events, EventType.DISTRACTION)

		Double days = MetricsUtils.calculateNumberDaysInSample(timeline.durationInSeconds)


		GraphPoint<Double> point = null;

		if (disruptionEvents.size() > 0) {
			point = createTimelinePoint(timeline, [])
			point.value = MetricsUtils.roundOff( disruptionEvents.size() / days )
			point.danger = isOverThreshold(point.value)
		}

		return point
	}

	@Override
	GraphPoint<Double> createAggregatePoint(IdeaFlowTimeline timeline, List<GraphPoint<Double>> allPoints) {
		GraphPoint timelinePoint = null
		if (allPoints.size() > 0) {
			timelinePoint = analyzeIdeaFlowStory(timeline, [])
			timelinePoint.childPoints = allPoints
		}
		return timelinePoint
	}

	@Override
	MetricThreshold<Double> getDangerThreshold() {
		return createMetricThreshold(5D)
	}
}

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

import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.IdeaFlowStory
import org.openmastery.publisher.api.journey.SubtaskStory
import org.openmastery.publisher.api.journey.TagsUtil
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.storyweb.api.metrics.GraphPoint
import org.openmastery.publisher.api.metrics.MetricType
import org.openmastery.storyweb.api.metrics.MetricThreshold
import org.openmastery.time.TimeConverter

class WtfsPerDayAnalyzer extends AbstractTimelineAnalyzer<Double> {

	WtfsPerDayAnalyzer() {
		super(MetricType.WTFS_PER_DAY, false)
	}

	@Override
	GraphPoint<Double> analyzeIdeaFlowStory(IdeaFlowTimeline timeline, List<TroubleshootingJourney> journeys) {

		List<Event> wtfYayEvents = timeline.events.findAll() { Event event ->
			event.type == EventType.WTF || event.type == EventType.AWESOME
		}

		List<Event> wtfEvents = wtfYayEvents.findAll() { Event event ->
			event.type == EventType.WTF
		}

		Double days = calculateNumberDaysInSample(timeline.durationInSeconds)

		GraphPoint<Double> point = createTimelinePoint(timeline, [])
		point.painTags = extractPainTags(wtfYayEvents)
		point.value = roundOff( wtfEvents.size() / days )
		point.danger = isOverThreshold(point.value)

		return point
	}

	double roundOff(double rawDouble) {
		return Math.round(rawDouble * 100)/100
	}

	double calculateNumberDaysInSample(long durationInSeconds) {
		double days = durationInSeconds.toDouble() / (60 * 60 * 6)
		return Math.max(days, 1);
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

	Set<String> extractPainTags(List<Event> wtfYayEvents) {
		Set<String> painTags = new HashSet<>()
		wtfYayEvents.each { Event event ->
			painTags.addAll(TagsUtil.extractUniqueHashTags(event.comment))
		}
		return painTags
	}

	@Override
	MetricThreshold<Double> getDangerThreshold() {
		return createMetricThreshold(10D)
	}
}

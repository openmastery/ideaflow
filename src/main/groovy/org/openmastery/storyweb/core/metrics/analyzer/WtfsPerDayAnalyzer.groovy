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
		super(MetricType.WTFS_PER_DAY, true)
	}

	@Override
	GraphPoint<Double> analyzeIdeaFlowStory(IdeaFlowTimeline timeline, List<TroubleshootingJourney> journeys) {

		List<Event> wtfYayEvents = timeline.events.findAll() { Event event ->
			event.type == EventType.WTF || event.type == EventType.AWESOME
		}

		LocalDate start = timeline.start.toLocalDate()
		LocalDate end = timeline.end.toLocalDate()

		List<GraphPoint<Double>> allPoints = calculateWtfsPerDay(start, end, wtfYayEvents)

		return createAggregatePoint(timeline, allPoints)
	}

	@Override
	GraphPoint<Double> assignBlameToSubtasks(IdeaFlowStory ideaFlowStory, GraphPoint<Double> summaryPoint) {

		ideaFlowStory.subtasks.each { SubtaskStory subtaskStory ->
			GraphPoint<Double> subtaskPoint = analyzeIdeaFlowStory(subtaskStory.timeline, subtaskStory.troubleshootingJourneys)
			if (subtaskPoint) {
				subtaskPoint.relativePath = subtaskStory.relativePath
				subtaskPoint.contextTags = subtaskStory.contextTags
				summaryPoint.childPoints.add(subtaskPoint)
			}
		}

		return summaryPoint
	}


	@Override
	GraphPoint<Double> createAggregatePoint(IdeaFlowTimeline timeline, List<GraphPoint<Double>> allPoints) {
		GraphPoint timelinePoint = null
		if (allPoints.size() > 0) {

			timelinePoint = createTimelinePoint(timeline, allPoints)
			timelinePoint.value = getMaximumValue(allPoints)
			timelinePoint.danger = isOverThreshold(timelinePoint.value)
		}
		return timelinePoint
	}

	private List<GraphPoint<Double>> calculateWtfsPerDay(LocalDate start, LocalDate end, List<Event> wtfYayEvents) {
		List<GraphPoint<Double>> allPoints = []

		for (LocalDate currentdate = start; currentdate.isBefore(end) || currentdate.isEqual(end);
			 currentdate = currentdate.plusDays(1)) {

			List<Event> dailyWtfs = findWtfsForTheDay(wtfYayEvents, currentdate)
			Set<String> painTags = extractPainTagsForTheDay(wtfYayEvents, currentdate)

			if (dailyWtfs.size() > 0) {
				String formattedDate = TimeConverter.formatDate(currentdate)
				GraphPoint<Double> dailyWtfsPoint = createEmptyPoint("/day/" + formattedDate)
				dailyWtfsPoint.distance = 60 * 60 * 24
				dailyWtfsPoint.position = currentdate.toLocalDateTime(new LocalTime(0))
				dailyWtfsPoint.relativePositionInSeconds = findFirstRelativePositionWithMatchingDate(dailyWtfs, currentdate)
				dailyWtfsPoint.painTags = painTags
				dailyWtfsPoint.value = dailyWtfs.size()
				dailyWtfsPoint.danger = isOverThreshold(dailyWtfsPoint.value)

				allPoints.add(dailyWtfsPoint)
			}
		}

		return allPoints
	}

	long findFirstRelativePositionWithMatchingDate(List<Event> wtfEvents, LocalDate localDate) {
		Event event = wtfEvents.find() { Event wtf ->
			wtf.position.toLocalDate().equals(localDate)
		}
		event.relativePositionInSeconds
	}

	List<Event> findWtfsForTheDay(List<Event> wtfYayEvents, LocalDate currentDate) {
		wtfYayEvents.findAll() { Event wtf ->
			wtf.position.toLocalDate().equals(currentDate) && wtf.type == EventType.WTF
		}
	}

	Set<String> extractPainTagsForTheDay(List<Event> wtfYayEvents, LocalDate currentDate) {
		Set<String> painTags = new HashSet<>()
		wtfYayEvents.findAll() { Event wtf ->
			wtf.position.toLocalDate().equals(currentDate)
		}.each {
			painTags.addAll(TagsUtil.extractUniqueHashTags(it.comment))
		}
		return painTags
	}

	@Override
	MetricThreshold<Double> getDangerThreshold() {
		return createMetricThreshold(10D)
	}
}

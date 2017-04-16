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
package org.openmastery.publisher.ideaflow.story

import org.openmastery.mapper.ValueObjectMapper
import org.openmastery.publisher.api.Interval
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.ActivitySummary
import org.openmastery.publisher.api.ideaflow.Haystack
import org.openmastery.publisher.core.activity.ActivityEntity
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.mapper.EventMapper
import org.openmastery.publisher.core.mapper.ExecutionEventMapper
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.publisher.ideaflow.IntervalSplitter
import org.openmastery.publisher.ideaflow.timeline.RelativeTimeProcessor

import java.time.Duration
import java.time.LocalDateTime

class HaystackListGenerator {

	public static final String ACTIVITY_TYPE_EDITOR = "editor"
	public static final String ACTIVITY_TYPE_EXTERNAL = "external"


	private EventMapper eventMapper = new EventMapper()
	private ExecutionEventMapper executionEventMapper = new ExecutionEventMapper()
	private ValueObjectMapper valueObjectMapper = new ValueObjectMapper()

	private RelativeTimeProcessor relativeTimeProcessor
	private LocalDateTime taskStart
	private List<EditorActivityEntity> editorActivities
	private List<ExternalActivityEntity> externalActivities
	private List<ExecutionActivityEntity> executionActivities
	private List<IdleTimeBandModel> idleTimeBands
	private List<Event> subtaskEvents

	HaystackListGenerator(RelativeTimeProcessor relativeTimeProcessor) {
		this.relativeTimeProcessor = relativeTimeProcessor
	}

	HaystackListGenerator taskStart(LocalDateTime taskStart) {
		this.taskStart = taskStart
		this
	}

	HaystackListGenerator idleActivities(List<IdleActivityEntity> idleActivities) {
		this.idleTimeBands = valueObjectMapper.mapList(idleActivities, IdleTimeBandModel)
		this
	}

	HaystackListGenerator editorActivities(List<EditorActivityEntity> editorActivities) {
		this.editorActivities = editorActivities
		this
	}

	HaystackListGenerator externalActivities(List<ExternalActivityEntity> externalActivities) {
		this.externalActivities = externalActivities
		this
	}

	HaystackListGenerator executionActivities(List<ExecutionActivityEntity> executionActivities) {
		this.executionActivities = executionActivities
		this
	}

	HaystackListGenerator events(List<EventEntity> events) {
		List<EventEntity> subtaskEventEntityList = events.findAll { it.type == EventType.SUBTASK }
		this.subtaskEvents = eventMapper.mapList(subtaskEventEntityList)
		this
	}

	private List<ActivityInterval> getEditorAndExternalActivityIntervals() {
		List<ActivityInterval> activityIntervalList = []
		activityIntervalList += editorActivities.collect { new ActivityInterval(it) }
		activityIntervalList += externalActivities.collect { new ActivityInterval(it) }
		activityIntervalList
	}

	private List<ExecutionEvent> getHaystackBoundaryEventsAndCalculateRelativeTime(List<ActivityInterval> activityIntervalList) {
		List<ExecutionEvent> executionEventList = executionEventMapper.mapList(executionActivities)

		if (executionEventList.isEmpty() || taskStart.isBefore(executionEventList.first().start)) {
			ExecutionEvent firstHaystackEvent = new ExecutionEvent()
			firstHaystackEvent.start = taskStart
			firstHaystackEvent.durationInSeconds = 0
			executionEventList.add(firstHaystackEvent)
		}

		ExecutionEvent lastHaystackEvent = new ExecutionEvent()
		lastHaystackEvent.position = getLatestIntervalEnd(activityIntervalList)
		lastHaystackEvent.durationInSeconds = 0
		executionEventList.add(lastHaystackEvent)

		executionEventList.sort(PositionableComparator.INSTANCE)
		executionEventList
	}

	List<Haystack> generate() {
		List<ActivityInterval> activityIntervalList = getEditorAndExternalActivityIntervals()
		List<ExecutionEvent> executionEventList = getHaystackBoundaryEventsAndCalculateRelativeTime(activityIntervalList)

		List positionables = activityIntervalList + executionEventList + idleTimeBands
		relativeTimeProcessor.computeRelativeTime(positionables)

		List<Haystack> haystackList = []
		ExecutionEvent haystackStart = null
		for (ExecutionEvent haystackEnd : executionEventList) {
			if (haystackStart != null) {
				haystackList << createHaystack(activityIntervalList, haystackStart, haystackEnd)
			}
			haystackStart = haystackEnd
		}

		haystackList
	}

	private Haystack createHaystack(List<ActivityInterval> activityIntervalList, ExecutionEvent haystackStart, ExecutionEvent haystackEnd) {
		Long haystackDurationInSeconds = haystackEnd.relativePositionInSeconds - haystackStart.relativePositionInSeconds

		List<ActivityInterval> haystackActivityIntervals = new IntervalSplitter<ActivityInterval>()
				.intervals(activityIntervalList)
				.intervalFactory(new ActivityIntervalFactory())
				.start(haystackStart.start)
				.end(haystackEnd.start)
				.relativePositionInSeconds(haystackStart.relativePositionInSeconds)
				.durationInSeconds(haystackDurationInSeconds)
				.split()

		Haystack haystack = new HaystackBuilder()
				.executionEvent(haystackStart)
				.durationInSeconds(haystackDurationInSeconds)
				.activityIntervals(haystackActivityIntervals)
				.create()
		haystack
	}

	private LocalDateTime getLatestIntervalEnd(List<ActivityInterval> activityIntervalList) {
		LocalDateTime latestIntervalEnd = taskStart
		for (ActivityInterval activityInterval : activityIntervalList) {
			if (activityInterval.end.isAfter(latestIntervalEnd)) {
				latestIntervalEnd = activityInterval.end
			}
		}
		latestIntervalEnd
	}


	private static class HaystackBuilder {

		private ExecutionEvent executionEvent
		private Long durationInSeconds
		private Map<String, ActivitySummary> keyToActivitySummaryMap = [:]

		HaystackBuilder executionEvent(ExecutionEvent executionEvent) {
			this.executionEvent = executionEvent
			this
		}

		HaystackBuilder durationInSeconds(Long durationInSeconds) {
			this.durationInSeconds = durationInSeconds
			this
		}

		HaystackBuilder activityIntervals(List<ActivityInterval> activityIntervals) {
			for (ActivityInterval activityInterval : activityIntervals) {
				if (activityInterval.durationInSeconds < 1) {
					continue
				}

				ActivitySummary summary = activityInterval.createActivitySummary()
				ActivitySummary existingSummary = keyToActivitySummaryMap[activityInterval.key]
				if (existingSummary == null) {
					keyToActivitySummaryMap[activityInterval.key] = summary
				} else {
					existingSummary.aggregate(summary)
				}
			}
			this
		}

		Haystack create() {
			String relativePath = executionEvent.id != null ? "/haystack/${executionEvent.id}" : null
			Haystack.builder()
					.relativePath(relativePath)
					.position(executionEvent.position)
					.relativePositionInSeconds(executionEvent.relativePositionInSeconds)
					.durationInSeconds(durationInSeconds)
					.executionDurationInSeconds(executionEvent.durationInSeconds)
					.processName(executionEvent.processName)
					.executionTaskType(executionEvent.executionTaskType)
					.failed(executionEvent.failed)
					.debug(executionEvent.debug)
					.activitySummaries(getActivitySummaryList())
					.build()
		}

		public List<ActivitySummary> getActivitySummaryList() {
			List<ActivitySummary> summaryList = keyToActivitySummaryMap.values() as List
			Collections.sort(summaryList, new Comparator<ActivitySummary>() {
				@Override
				public int compare(ActivitySummary summary1, ActivitySummary summary2) {
					summary1.totalDurationInSeconds.compareTo(summary2.totalDurationInSeconds)
				}
			})
			summaryList
		}

	}

	private static class ActivityInterval implements Interval {

		LocalDateTime start
		LocalDateTime end
		Duration duration
		Long relativePositionInSeconds

		private ActivityEntity activityEntity
		private String key
		private String activityType
		private String activityName
		private String activityDetail

		ActivityInterval(ActivityEntity activityEntity) {
			this.activityEntity = activityEntity
			start = activityEntity.start
			end = activityEntity.end
			duration = activityEntity.duration

			if (activityEntity instanceof EditorActivityEntity) {
				activityType = ACTIVITY_TYPE_EDITOR
				activityName = new File(activityEntity.filePath).name
				activityDetail = activityEntity.filePath
			} else if (activityEntity instanceof ExternalActivityEntity) {
				activityType = ACTIVITY_TYPE_EXTERNAL
				activityName = "External"
			} else {
				throw new IllegalStateException()
			}
			key = "${activityType}.${activityName}.${activityDetail}"
		}

		String getKey() {
			key
		}

		Long getDurationInSeconds() {
			duration.seconds
		}

		boolean isModified() {
			(activityEntity instanceof EditorActivityEntity) && activityEntity.modified
		}

		LocalDateTime getPosition() {
			start
		}

		ActivitySummary createActivitySummary() {
			ActivitySummary.builder()
					.activityType(activityType)
					.activityName(activityName)
					.activityDetail(activityDetail)
					.totalDurationInSeconds(durationInSeconds)
					.modifiedDurationInSeconds(isModified() ? durationInSeconds : 0L)
					.build()
		}

	}

	private static class ActivityIntervalFactory implements Interval.Factory<ActivityInterval> {

		@Override
		ActivityInterval create(ActivityInterval interval, LocalDateTime start, LocalDateTime end, Long relativePositionInSeconds, Long durationInSeconds) {
			ActivityInterval newInterval = new ActivityInterval(interval.activityEntity)
			newInterval.start = start
			newInterval.end = end
			newInterval.relativePositionInSeconds = relativePositionInSeconds
			newInterval.duration = Duration.ofSeconds(durationInSeconds)
			newInterval
		}

	}

}

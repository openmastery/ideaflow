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
package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.LocalDateTime
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.Interval
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.api.activity.BlockActivity
import org.openmastery.publisher.api.activity.ModificationActivity
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.activity.BlockActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel
import org.openmastery.time.TimeConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

class IdeaFlowTaskTimelineGenerator {

	private Task task

	private List<Event> events = []
	private List<IdleTimeBandModel> idleTimeBands = []
	private List<ModificationActivity> modificationActivities = []
	private List<ExecutionEvent> executionEvents = []
	private List<BlockActivity> blockActivities = []

	private EntityMapper entityMapper = new EntityMapper()
	private IdeaFlowBandGenerator bandGenerator
	private int strategyBandMinimumDurationInMinutes

	IdeaFlowTaskTimelineGenerator(IdeaFlowBandGenerator bandGenerator) {
		this.bandGenerator = bandGenerator
		this.strategyBandMinimumDurationInMinutes = bandGenerator.strategyBandMinimumDurationInMinutes
	}

	IdeaFlowTaskTimelineGenerator task(Task task) {
		this.task = task
		this
	}

	IdeaFlowTaskTimelineGenerator idleActivities(List<IdleActivityEntity> idleActivities) {
		this.idleTimeBands = entityMapper.mapList(idleActivities, IdleTimeBandModel)
		this
	}

	IdeaFlowTaskTimelineGenerator events(List<EventEntity> events) {
		this.events = entityMapper.mapList(events, Event)
		this
	}

	IdeaFlowTaskTimelineGenerator executionActivities(List<ExecutionActivityEntity> executionActivities) {
		this.executionEvents = executionActivities.collect { ExecutionActivityEntity entity ->
			ExecutionEvent execution = entityMapper.mapIfNotNull(entity, ExecutionEvent)
			execution.failed = entity.exitCode != 0
			execution.durationInSeconds = TimeConverter.between(entity.start, entity.end).standardSeconds
			return execution
		}
		this
	}

	IdeaFlowTaskTimelineGenerator modificationActivities(List<ModificationActivityEntity> modificationActivities) {
		this.modificationActivities = entityMapper.mapList(modificationActivities, ModificationActivity)
		this
	}


	IdeaFlowTaskTimelineGenerator blockActivities(List<BlockActivityEntity> blockActivityEntities) {
		this.blockActivities = entityMapper.mapList(blockActivityEntities, BlockActivity)
		this
	}

	IdeaFlowTaskTimeline generate() {
		List<IdeaFlowBandModel> ideaFlowBands = generateIdeaFlowBands()

		// NOTE: calendar events MUST be added BEFORE relative time is computed
		addCalendarEvents(ideaFlowBands)

		//when no modification activity above threshold, create learning bands
		//when WTF, WTF, AWESOME combo, create conflict band that spans this time

		collapseIdleTime(ideaFlowBands)
		convertLearningBandsUnderMinimumThresholdToProgress(ideaFlowBands)
		computeRelativeTime(ideaFlowBands)

		return createIdeaFlowTimeline(ideaFlowBands)
	}

	void convertLearningBandsUnderMinimumThresholdToProgress(List<IdeaFlowBandModel> ideaFlowBandModels) {
		ideaFlowBandModels.each { IdeaFlowBandModel band ->
			if (band.type == IdeaFlowStateType.LEARNING && (band.duration.standardMinutes < strategyBandMinimumDurationInMinutes)) {
				band.type = IdeaFlowStateType.PROGRESS
			}
		}
	}

	private List<IdeaFlowBandModel> generateIdeaFlowBands() {
		List<Positionable> positionables = getAllItemsAsPositionableList()
		bandGenerator.generateIdeaFlowBands(positionables)
	}

	private void collapseIdleTime(List<IdeaFlowBandModel> ideaFlowBands) {
		IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
		idleTimeBands = idleTimeProcessor.collapseIdleTime(ideaFlowBands, idleTimeBands, events)
	}

	private void computeRelativeTime(List<IdeaFlowBandModel> ideaFlowBands) {
		List<Positionable> positionables = getAllItemsAsPositionableList()
		positionables.addAll(getIdeaFlowBandsWithContents(ideaFlowBands))

		RelativeTimeProcessor relativeTimeProcessor = new RelativeTimeProcessor()
		relativeTimeProcessor.computeRelativeTime(positionables)
	}

	Set<Positionable> getIdeaFlowBandsWithContents(List<IdeaFlowBandModel> ideaFlowBands) {
		Set<Positionable> positionableSet = []
		ideaFlowBands.each { IdeaFlowBandModel model ->
			positionableSet.add(model)
			positionableSet.addAll(model.getAllContentsFlattenedAsPositionableList())
		}
		positionableSet
	}

	private List getAllItemsAsPositionableList() {
		List<Positionable> positionables = []
		positionables.addAll(events)
		positionables.addAll(executionEvents)
		positionables.addAll(modificationActivities)
		positionables.addAll(blockActivities)
		positionables
	}

	private void addCalendarEvents(List<IdeaFlowBandModel> ideaFlowBands) {
		List<Interval> intervals = []
		intervals.addAll(ideaFlowBands)
		intervals.addAll(idleTimeBands)

		CalendarEventGenerator calendarEventGenerator = new CalendarEventGenerator()
		List<Event> calendarEvents = calendarEventGenerator.generateCalendarEvents(intervals)

		events.addAll(calendarEvents)
	}

	private IdeaFlowTaskTimeline createIdeaFlowTimeline(List<IdeaFlowBandModel> ideaFlowBandModels) {
		List<IdeaFlowBand> ideaFlowBands = entityMapper.mapList(ideaFlowBandModels, IdeaFlowBand)
		if (ideaFlowBands.isEmpty()) {
			return null
		}

		Collections.sort(ideaFlowBands, PositionableComparator.INSTANCE);
		IdeaFlowBand firstBand = ideaFlowBands.first()
		IdeaFlowBand lastBand = ideaFlowBands.last()
		Long totalDuration = (lastBand.relativePositionInSeconds - firstBand.relativePositionInSeconds) + lastBand.durationInSeconds

		addInitialStrategySubtaskEventAndSortEventsList(task.id, firstBand.start)

		Collections.sort(executionEvents, PositionableComparator.INSTANCE);
		Collections.sort(modificationActivities, PositionableComparator.INSTANCE);
		Collections.sort(blockActivities, PositionableComparator.INSTANCE)

		return IdeaFlowTaskTimeline.builder()
				.task(task)
				.start(firstBand.start)
				.end(lastBand.end)
				.relativePositionInSeconds(firstBand.relativePositionInSeconds)
				.durationInSeconds(totalDuration)
				.events(events)
				.executionEvents(executionEvents)
				.ideaFlowBands(ideaFlowBands)
				.build()
	}

	private void addInitialStrategySubtaskEventAndSortEventsList(Long taskId, LocalDateTime timelineStart) {
		Collections.sort(events, PositionableComparator.INSTANCE);
		Event firstSubtaskEvent = events.find { it.type == EventType.SUBTASK }
		if ((firstSubtaskEvent != null) && firstSubtaskEvent.position.isEqual(timelineStart)) {
			return
		}

		Event initialStrategySubtaskEvent = Event.builder()
				.id(-1)
				.type(EventType.SUBTASK)
				.comment("Initial Strategy")
				.build()
		initialStrategySubtaskEvent.taskId = taskId
		initialStrategySubtaskEvent.position = timelineStart
		initialStrategySubtaskEvent.relativePositionInSeconds = 0

		events.add(initialStrategySubtaskEvent)
		Collections.sort(events, PositionableComparator.INSTANCE);
	}


	@Component
	public static class Factory {

		@Autowired
		private IdeaFlowBandGenerator bandGenerator = new IdeaFlowBandGenerator()

		public IdeaFlowTaskTimelineGenerator create() {
			return new IdeaFlowTaskTimelineGenerator(bandGenerator)
		}

	}

}

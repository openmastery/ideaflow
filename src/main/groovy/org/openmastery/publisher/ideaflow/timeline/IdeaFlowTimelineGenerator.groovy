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
package org.openmastery.publisher.ideaflow.timeline

import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.Interval
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.activity.BlockActivity
import org.openmastery.publisher.api.activity.ModificationActivity
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.core.activity.BlockActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.core.timeline.IdleTimeBandModel

class IdeaFlowTimelineGenerator {

	private Task task

	private List<Event> events = []
	private List<IdleTimeBandModel> idleTimeBands = []
	private List<ModificationActivity> modificationActivities = []
	private List<ExecutionEvent> executionEvents = []
	private List<BlockActivity> blockActivities = []

	private EntityMapper entityMapper = new EntityMapper()

	IdeaFlowTimelineGenerator task(Task task) {
		this.task = task
		this
	}

	IdeaFlowTimelineGenerator idleActivities(List<IdleActivityEntity> idleActivities) {
		this.idleTimeBands = entityMapper.mapList(idleActivities, IdleTimeBandModel)
		this
	}

	IdeaFlowTimelineGenerator events(List<EventEntity> events) {
		this.events = entityMapper.mapList(events, Event)
		this
	}

	IdeaFlowTimelineGenerator executionActivities(List<ExecutionActivityEntity> executionActivities) {
		this.executionEvents = executionActivities.collect { ExecutionActivityEntity entity ->
			ExecutionEvent execution = entityMapper.mapIfNotNull(entity, ExecutionEvent)
			execution.failed = entity.exitCode != 0
			return execution
		}
		this
	}

	IdeaFlowTimelineGenerator modificationActivities(List<ModificationActivityEntity> modificationActivities) {
		this.modificationActivities = entityMapper.mapList(modificationActivities, ModificationActivity)
		this
	}

	IdeaFlowTimelineGenerator blockActivities(List<BlockActivityEntity> blockActivityEntities) {
		this.blockActivities = entityMapper.mapList(blockActivityEntities, BlockActivity)
		this
	}

	IdeaFlowTimeline generate() {
		List<IdeaFlowBandModel> ideaFlowBands = generateIdeaFlowBandsBands()

		// NOTE: calendar events MUST be added BEFORE relative time is computed
		addCalendarEvents(ideaFlowBands)

		//when no modification activity above threshold, create learning bands
		//when WTF, WTF, AWESOME combo, create conflict band that spans this time

		collapseIdleTime(ideaFlowBands)
		computeRelativeTime(ideaFlowBands)

		return createIdeaFlowTimeline(ideaFlowBands)
	}

	private List<IdeaFlowBandModel> generateIdeaFlowBandsBands() {
		IdeaFlowBandGenerator bandGenerator = new IdeaFlowBandGenerator()

		List<Positionable> positionables = getAllItemsAsPositionableList()
		bandGenerator.generateIdeaFlowBands(positionables)
	}

	private void collapseIdleTime(List<IdeaFlowBandModel> ideaFlowBands) {
		if (idleTimeBands) {
			IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
			idleTimeProcessor.collapseIdleTime(ideaFlowBands, idleTimeBands)
		}
	}

	private void computeRelativeTime(List<IdeaFlowBandModel> ideaFlowBands) {
		List<Positionable> positionables = getAllItemsAsPositionableList()
		ideaFlowBands.each { IdeaFlowBandModel model ->
			positionables.add(model)
			positionables.addAll(model.getAllContentsFlattenedAsPositionableList())
		}

		RelativeTimeProcessor relativeTimeProcessor = new RelativeTimeProcessor()
		relativeTimeProcessor.computeRelativeTime(positionables)
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

	private IdeaFlowTimeline createIdeaFlowTimeline(List<IdeaFlowBandModel> ideaFlowBandModels) {
		List<IdeaFlowBand> ideaFlowBands = entityMapper.mapList(ideaFlowBandModels, IdeaFlowBand)

		if (ideaFlowBands.isEmpty()) {
			return IdeaFlowTimeline.builder()
					.task(task)
					.durationInSeconds(0)
					.build()
		}

		Collections.sort(events, PositionableComparator.INSTANCE);
		Collections.sort(executionEvents, PositionableComparator.INSTANCE);
		Collections.sort(modificationActivities, PositionableComparator.INSTANCE);
		Collections.sort(blockActivities, PositionableComparator.INSTANCE)
		Collections.sort(ideaFlowBands, PositionableComparator.INSTANCE);

		IdeaFlowBand firstBand = ideaFlowBands.first()
		IdeaFlowBand lastBand = ideaFlowBands.last()
		Long totalDuration = (lastBand.relativePositionInSeconds - firstBand.relativePositionInSeconds) + lastBand.durationInSeconds

		return IdeaFlowTimeline.builder()
				.task(task)
				.start(firstBand.start)
				.end(lastBand.end)
				.relativePositionInSeconds(firstBand.relativePositionInSeconds)
				.durationInSeconds(totalDuration)
				.events(events)
				.executionEvents(executionEvents)
				.modificationActivities(modificationActivities)
				.blockActivities(blockActivities)
				.ideaFlowBands(ideaFlowBands)
				.build()
	}


}

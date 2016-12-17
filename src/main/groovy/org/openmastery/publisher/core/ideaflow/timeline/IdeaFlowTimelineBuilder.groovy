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
package org.openmastery.publisher.core.ideaflow.timeline

import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.activity.ModificationActivity
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.PositionableComparator
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel

class IdeaFlowTimelineBuilder {

	private Task task

	private List<Event> events
	private List<IdleActivityEntity> idleActivities
	private List<ModificationActivity> modificationActivities
	private List<ExecutionEvent> executionEvents

	private EntityMapper entityMapper = new EntityMapper()
	private RelativeTimeProcessor relativeTimeProcessor = new RelativeTimeProcessor()

	IdeaFlowTimelineBuilder task(Task task) {
		this.task = task
		this
	}

	IdeaFlowTimelineBuilder idleActivities(List<IdleActivityEntity> idleActivities) {
		this.idleActivities = idleActivities
		this
	}

	IdeaFlowTimelineBuilder events(List<EventEntity> events) {
		this.events = entityMapper.mapList(events, Event)
		this
	}

	IdeaFlowTimelineBuilder executionActivities(List<ExecutionActivityEntity> executionActivities) {
		this.executionEvents = entityMapper.mapList(executionActivities, ExecutionEvent)
		this
	}

	IdeaFlowTimelineBuilder modificationActivities(List<ModificationActivityEntity> modificationActivities) {
		this.modificationActivities = entityMapper.mapList(modificationActivities, ModificationActivity)
		this
	}

	IdeaFlowTimeline build() {
		List<IdeaFlowBandModel> progressBands = generateProgressBands()
		collapseIdleTime(progressBands)
		computeRelativeTime(progressBands)

		//calendar events
		//when no modification activity above threshold, create learning bands
		//when WTF, WTF, AWESOME combo, create conflict band that spans this time

		return createIdeaFlowTimeline(progressBands)
	}

	private List<IdeaFlowBandModel> generateProgressBands() {
		List<Event> sortedTaskActivationEvents = createSortedTaskActivationEventList()
		List<IdeaFlowBandModel> progressBands = []
		IdeaFlowBandModel activeProgressBand = null

		sortedTaskActivationEvents.each { Event taskEvent ->
			if (activeProgressBand == null && taskEvent.type == EventType.ACTIVATE) {
				activeProgressBand = IdeaFlowBandModel.builder()
						.type(IdeaFlowStateType.PROGRESS)
						.taskId(taskEvent.id)
						.start(taskEvent.position)
						.nestedBands([])
						.idleBands([])
						.build()
			} else if (activeProgressBand != null && taskEvent.type == EventType.DEACTIVATE) {
				activeProgressBand.end = taskEvent.position
				progressBands.add(activeProgressBand)
				activeProgressBand = null
			} else {
				//eh... messed up state.  Multiple activates, multiple deactivates
				//should trigger a "repair" by looking at raw data and correcting events
			}
		}
		return progressBands
	}

	private List<Event> createSortedTaskActivationEventList() {
		List<Event> taskActivationEvents = events.findAll { Event event ->
			event.type == EventType.ACTIVATE || event.type == EventType.DEACTIVATE
		}
		Collections.sort(taskActivationEvents, PositionableComparator.INSTANCE);
		return taskActivationEvents
	}

	private void collapseIdleTime(List<IdeaFlowBandModel> progressBands) {
		if (idleActivities) {
			IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
			idleTimeProcessor.collapseIdleTime(progressBands, idleActivities)
		}
	}

	private void computeRelativeTime(List<IdeaFlowBandModel> progressBands) {
		List<Positionable> positionables = []
		positionables.addAll(events)
		positionables.addAll(executionEvents)
		positionables.addAll(modificationActivities)
		progressBands.each { IdeaFlowBandModel model ->
			positionables.add(model)
			positionables.addAll(model.getAllContentsFlattenedAsPositionableList())
		}
		relativeTimeProcessor.computeRelativeTime(positionables)
	}

	private IdeaFlowTimeline createIdeaFlowTimeline(List<IdeaFlowBandModel> progressBands) {
		List<IdeaFlowBand> ideaFlowBands = entityMapper.mapList(progressBands, IdeaFlowBand)
		Collections.sort(events, PositionableComparator.INSTANCE);
		Collections.sort(executionEvents, PositionableComparator.INSTANCE);
		Collections.sort(modificationActivities, PositionableComparator.INSTANCE);
		Collections.sort(ideaFlowBands, PositionableComparator.INSTANCE);

		return IdeaFlowTimeline.builder()
				.events(events)
				.executionEvents(executionEvents)
				.modificationActivities(modificationActivities)
				.ideaFlowBands(ideaFlowBands)
				.build()
	}

}

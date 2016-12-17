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

	private List<EventEntity> events
	private List<ModificationActivityEntity> modificationActivities
	private List<ExecutionActivityEntity> executionActivities
	private List<IdleActivityEntity> idleActivities

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
		this.events = events
		this
	}

	IdeaFlowTimelineBuilder modificationActivities(List<ModificationActivityEntity> modificationActivities) {
		this.modificationActivities = modificationActivities
		this
	}

	IdeaFlowTimeline build() {
		//if bands, collapse idle time within band, if idle time outside of band its chopped
		//translate execution activities to events
		//relative time, implement positionable

		List<Event> events = entityMapper.mapList(events, Event)
		List<ExecutionEvent> executionEvents = entityMapper.mapList(executionActivities, ExecutionEvent)
		List<ModificationActivity> modificationActivities = entityMapper.mapList(modificationActivities, ModificationActivity)
		List<IdeaFlowBandModel> progressBands = generateProgressBands()

		if (idleActivities) {
			// TODO: fix
//			IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
//			idleTimeProcessor.collapseIdleTime(segment, idleActivities)
		}

		computeRelativeTime(events, executionEvents, modificationActivities, progressBands)

		//when no modification activity above threshold, create learning bands
		//when WTF, WTF, AWESOME combo, create conflict band that spans this time

		List<IdeaFlowBand> ideaFlowBands = entityMapper.mapList(progressBands, IdeaFlowBand)
		return IdeaFlowTimeline.builder()
				.events(events)
				.executionEvents(executionEvents)
				.modificationActivities(modificationActivities)
				.ideaFlowBands(ideaFlowBands)
				.build()
	}

	private void computeRelativeTime(List<Event> events, List<ExecutionEvent> executionEvents,
	                                 List<ModificationActivity> modificationActivities, List<IdeaFlowBandModel> progressBands) {
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

	List<IdeaFlowBandModel> generateProgressBands() {
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

	List<Event> createSortedTaskActivationEventList() {
		List<Event> allEvents = entityMapper.mapList(events, Event)
		List<Event> taskActivationEvents = allEvents.findAll { Event event ->
			event.type == EventType.ACTIVATE || event.type == EventType.DEACTIVATE
		}
		Collections.sort(taskActivationEvents, PositionableComparator.INSTANCE);
		return taskActivationEvents
	}

}

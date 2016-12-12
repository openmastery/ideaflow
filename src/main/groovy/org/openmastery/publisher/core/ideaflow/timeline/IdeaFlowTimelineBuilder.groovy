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

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.PositionableComparator
import org.openmastery.publisher.core.activity.*
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.event.EventModel
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel
import org.openmastery.time.TimeConverter

class IdeaFlowTimelineBuilder {

	private Task task

	private List<IdeaFlowBand> progressBands
	private List<EventEntity> events
	private List<IdleActivityEntity> idleActivities
	private List<ModificationActivityEntity> modificationActivities

	private List<ExternalActivityEntity> externalActivities //TODO do we need to convert to idle?


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

	IdeaFlowTimelineBuilder externalActivities(List<ExternalActivityEntity> externalActivities) {
		this.externalActivities = externalActivities
		this
	}

	IdeaFlowTimelineBuilder modificationActivities(List<ModificationActivityEntity> modificationActivities) {
		this.modificationActivities = modificationActivities
		this
	}

	IdeaFlowTimeline build() {
		List<IdeaFlowBandModel> progressBands = generateProgressBands()


		IdeaFlowTimeline timeline = createTimelineAndCollapseIdleTime()
		//computeRelativeTime(segment.getAllContentsFlattenedAsPositionableList())
		timeline
	}

	List<IdeaFlowBandModel> generateProgressBands() {
		List<EventModel> sortedTaskActivationEvents = createSortedTaskActivationEventList()
		List<IdeaFlowBandModel> progressBands = []
		IdeaFlowBandModel activeProgressBand = null

		sortedTaskActivationEvents.each { EventModel taskEvent ->
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

//	private long id;
//	private Long taskId;
//
//	private LocalDateTime start;
//	private LocalDateTime end;
//	private Long durationInSeconds;
//	private Long relativePositionInSeconds;
//
//	private String startingComment;
//	private String endingComent;
//
//	private IdeaFlowStateType type;


	List<EventModel> createSortedTaskActivationEventList() {
		List<EventEntity> taskActivationEvents = events.findAll { EventEntity event ->
			event.type == EventType.ACTIVATE || event.type == EventType.DEACTIVATE
		}
		List<EventModel> positionableTaskActivationEvents = toEventModelList(taskActivationEvents)
		Collections.sort(positionableTaskActivationEvents, PositionableComparator.INSTANCE);
		return positionableTaskActivationEvents
	}




	private List<EventModel> toEventModelList(List<EventEntity> eventEntityList) {
		if (eventEntityList == null) {
			return []
		}

		eventEntityList.collect { EventEntity eventEntity ->
			new EventModel(eventEntity)
		}
	}

}

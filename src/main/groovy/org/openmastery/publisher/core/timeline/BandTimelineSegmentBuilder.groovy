package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.event.EventModel
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity


class BandTimelineSegmentBuilder {

	private String description
	private List<IdeaFlowStateEntity> ideaFlowStates
	private List<EventEntity> events
	private List<IdleActivityEntity> idleActivities

	BandTimelineSegmentBuilder(List<IdeaFlowStateEntity> ideaFlowStates, List<EventEntity> events) {
		this.ideaFlowStates = ideaFlowStates
		this.events = events
	}

	BandTimelineSegmentBuilder description(String description) {
		this.description = description
		this
	}

	BandTimelineSegmentBuilder collapseIdleTime(List<IdleActivityEntity> idleActivities) {
		this.idleActivities = idleActivities
		this
	}

	BandTimelineSegment build() {
		BandTimelineSegment segment = createTimelineSegment(ideaFlowStates, events)
		if (description) {
			segment.setDescription(description)
		}
		if (idleActivities) {
			IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
			idleTimeProcessor.collapseIdleTime(segment, idleActivities)
		}
		segment
	}



	// TODO: refactor... AAHHHHH!!!!!

	private BandTimelineSegment createTimelineSegment(List<IdeaFlowStateEntity> ideaFlowStates, List<EventEntity> events) {
		ideaFlowStates = new ArrayList<>(ideaFlowStates)
		Collections.sort(ideaFlowStates)

		IdeaFlowBandModel previousBand = null
		TimeBandGroupModel activeTimeBandGroup = null
		List<IdeaFlowBandModel> ideaFlowBands = []
		List<TimeBandGroupModel> ideaFlowBandGroups = []
		for (IdeaFlowStateEntity state : ideaFlowStates) {
			IdeaFlowBandModel timeBand = toIdeaFlowBandModel(state)

			if (state.isNested()) {
				previousBand.addNestedBand(timeBand)
			} else {
				if (state.isLinkedToPrevious() && (ideaFlowBands.isEmpty() == false)) {
					if (activeTimeBandGroup == null) {
						IdeaFlowBandModel firstBandInGroup = ideaFlowBands.remove(ideaFlowBands.size() - 1)
						activeTimeBandGroup = TimeBandGroupModel.builder()
								.id("group-${firstBandInGroup.id}")
								.linkedTimeBands([])
								.build()

						activeTimeBandGroup.addLinkedTimeBand(firstBandInGroup)
						ideaFlowBandGroups.add(activeTimeBandGroup)
					}

					activeTimeBandGroup.addLinkedTimeBand(timeBand)
				} else {
					activeTimeBandGroup = null
					ideaFlowBands.add(timeBand)
				}

				if (previousBand != null) {
					if (previousBand.end.isAfter(timeBand.start)) {
						previousBand.end = timeBand.start
					}
				}

				previousBand = timeBand
			}
		}

		BandTimelineSegment segment = BandTimelineSegment.builder()
				.ideaFlowBands(ideaFlowBands)
				.timeBandGroups(ideaFlowBandGroups)
				.events(toEventList(events))
				.build()

		return segment;
	}

	private IdeaFlowBandModel toIdeaFlowBandModel(IdeaFlowStateEntity state) {
		IdeaFlowBandModel.builder()
				.id(state.id)
				.taskId(state.taskId)
				.type(state.type)
				.start(state.start)
				.end(state.end)
				.startingComment(state.startingComment)
				.endingComent(state.endingComment)
				.idleBands([])
				.nestedBands([])
				.build()
	}

	private List<EventModel> toEventList(List<EventEntity> eventEntityList) {
		eventEntityList.collect { EventEntity eventEntity ->
			toEvent(eventEntity)
		}
	}

	private EventModel toEvent(EventEntity subtask) {
		new EventModel(subtask)
	}

}

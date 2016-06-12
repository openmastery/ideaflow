package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity

class BandTimelineSegmentFactory {

	// TODO: refactor... AAHHHHH!!!!!

	public BandTimelineSegment createTimelineSegment(List<IdeaFlowStateEntity> ideaFlowStates, List<EventEntity> events) {
		ideaFlowStates = new ArrayList<>(ideaFlowStates);
		Collections.sort(ideaFlowStates)

		IdeaFlowBandModel previousBand = null;
		TimeBandGroupModel activeTimeBandGroup = null;
		ArrayList<IdeaFlowBandModel> ideaFlowBands = new ArrayList<>();
		ArrayList<TimeBandGroupModel> ideaFlowBandGroups = new ArrayList<>();
		for (IdeaFlowStateEntity state : ideaFlowStates) {
			IdeaFlowBandModel timeBand = IdeaFlowBandModel.builder()
					.id(state.id)
					.taskId(state.taskId)
					.type(state.type)
					.start(state.start)
					.end(state.end)
					.startingComment(state.startingComment)
					.endingComent(state.endingComment)
					.idleBands(new ArrayList<IdleTimeBandModel>())
					.nestedBands(new ArrayList<IdeaFlowBandModel>())
					.build()

			if (state.isNested()) {
				previousBand.addNestedBand(timeBand)
			} else {
				if (state.isLinkedToPrevious() && (ideaFlowBands.isEmpty() == false)) {
					if (activeTimeBandGroup == null) {
						IdeaFlowBandModel firstBandInGroup = ideaFlowBands.remove(ideaFlowBands.size() - 1)
						activeTimeBandGroup = TimeBandGroupModel.builder()
								.id("group-${firstBandInGroup.id}")
								.linkedTimeBands(new ArrayList<IdeaFlowBandModel>())
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
				.build();

		return segment;
	}

	private List<Event> toEventList(List<EventEntity> eventEntityList) {
		eventEntityList.collect { EventEntity eventEntity ->
			toEvent(eventEntity)
		}
	}

	private Event toEvent(EventEntity subtask) {
		Event subtaskEvent = Event.builder()
				.id(subtask.id)
				.taskId(subtask.taskId)
				.position(subtask.position)
				.comment(subtask.comment)
				.eventType(subtask.type)
				.build()
		subtaskEvent
	}


}

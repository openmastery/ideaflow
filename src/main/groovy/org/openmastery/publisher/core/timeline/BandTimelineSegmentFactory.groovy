package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.timeline.TimeBandGroup
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity
import org.openmastery.publisher.api.timeline.IdleTimeBand

class BandTimelineSegmentFactory {

	// TODO: refactor... AAHHHHH!!!!!

	public BandTimelineSegment createTimelineSegment(List<IdeaFlowStateEntity> ideaFlowStates, List<EventEntity> events) {
		ideaFlowStates = new ArrayList<>(ideaFlowStates);
		Collections.sort(ideaFlowStates)

		IdeaFlowBand previousBand = null;
		TimeBandGroup activeTimeBandGroup = null;
		ArrayList<IdeaFlowBand> ideaFlowBands = new ArrayList<>();
		ArrayList<TimeBandGroup> ideaFlowBandGroups = new ArrayList<>();
		for (IdeaFlowStateEntity state : ideaFlowStates) {
			IdeaFlowBand timeBand = IdeaFlowBand.builder()
					.id(state.id)
					.taskId(state.taskId)
					.type(state.type)
					.start(state.start)
					.end(state.end)
					.startingComment(state.startingComment)
					.endingComent(state.endingComment)
					.idleBands(new ArrayList<IdleTimeBand>())
					.nestedBands(new ArrayList<IdeaFlowBand>())
					.build()

			if (state.isNested()) {
				previousBand.addNestedBand(timeBand)
			} else {
				if (state.isLinkedToPrevious() && (ideaFlowBands.isEmpty() == false)) {
					if (activeTimeBandGroup == null) {
						IdeaFlowBand firstBandInGroup = ideaFlowBands.remove(ideaFlowBands.size() - 1)
						activeTimeBandGroup = TimeBandGroup.builder()
								.id("group-${firstBandInGroup.id}")
								.linkedTimeBands(new ArrayList<IdeaFlowBand>())
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

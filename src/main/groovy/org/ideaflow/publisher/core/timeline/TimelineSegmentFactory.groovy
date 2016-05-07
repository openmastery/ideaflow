package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.event.Event
import org.ideaflow.publisher.api.ideaflow.IdeaFlowBand
import org.ideaflow.publisher.api.timeline.IdleTimeBand
import org.ideaflow.publisher.api.timeline.TimeBandGroup
import org.ideaflow.publisher.api.timeline.TimelineSegment
import org.ideaflow.publisher.core.event.EventEntity
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateEntity

class TimelineSegmentFactory {

	// TODO: refactor... AAHHHHH!!!!!

	public TimelineSegment createTimelineSegment(List<IdeaFlowStateEntity> ideaFlowStates, List<EventEntity> events) {
		ideaFlowStates = new ArrayList<>(ideaFlowStates);
		Collections.sort(ideaFlowStates)

		IdeaFlowBand previousBand = null;
		TimeBandGroup activeTimeBandGroup = null;
		ArrayList<IdeaFlowBand> ideaFlowBands = new ArrayList<>();
		ArrayList<TimeBandGroup> ideaFlowBandGroups = new ArrayList<>();
		for (IdeaFlowStateEntity state : ideaFlowStates) {
			IdeaFlowBand timeBand = IdeaFlowBand.builder()
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
						activeTimeBandGroup = TimeBandGroup.builder()
								.linkedTimeBands(new ArrayList<IdeaFlowBand>())
								.build()

						IdeaFlowBand firstBandInGroup = ideaFlowBands.remove(ideaFlowBands.size() - 1)
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

		TimelineSegment segment = TimelineSegment.builder()
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
				.eventType(subtask.eventType)
				.build()
		subtaskEvent
	}


}

package org.openmastery.publisher.api.ideaflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.Positionable;
import org.openmastery.publisher.api.activity.BlockActivity;
import org.openmastery.publisher.api.activity.ModificationActivity;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.EventType;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.task.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaFlowTimeline implements Positionable {

	private Task task;

	private LocalDateTime start;
	private LocalDateTime end;

	private Long durationInSeconds;
	private Long relativePositionInSeconds; //can be offset if showing a subtask fragment

	private List<IdeaFlowBand> ideaFlowBands;
	private List<ModificationActivity> modificationActivities;
	private List<BlockActivity> blockActivities;
	private List<ExecutionEvent> executionEvents;
	private List<Event> events;


	@Override
	public LocalDateTime getPosition() {
		return start;
	}

	@JsonIgnore
	public List<IdeaFlowTimeline> splitBySubtask() {
		List<Event> subtaskEvents = events.stream()
				.filter(e -> e.getType() == EventType.SUBTASK)
				.collect(Collectors.toList());

		if (subtaskEvents.isEmpty()) {
			return getThisTimelineAsList();
		} else {
			if (subtaskEvents.size() == 1) {
				LocalDateTime position = subtaskEvents.get(0).getPosition();
				if (position.isEqual(start) || position.isEqual(end)) {
					return getThisTimelineAsList();
				}
			}
			return splitBySubtask(subtaskEvents);
		}
	}

	@JsonIgnore
	private List<IdeaFlowTimeline> getThisTimelineAsList() {
		List<IdeaFlowTimeline> subtaskTimelines = new ArrayList<>();
		subtaskTimelines.add(this);
		return subtaskTimelines;
	}

	private List<IdeaFlowTimeline> splitBySubtask(List<Event> subtaskEvents) {
		Event previousSubtask = null;
		ItemFilter filter = new ItemFilter(false);
		List<IdeaFlowTimeline> subtaskTimelines = new ArrayList<>();

		for (Event subtask : subtaskEvents) {
			if (previousSubtask != null) {
				Long durationInSeconds = subtask.getRelativePositionInSeconds() - previousSubtask.getRelativePositionInSeconds();
				IdeaFlowTimeline subtaskTimeline = splitTimeline(filter, previousSubtask.getPosition(), subtask.getPosition(), previousSubtask.getRelativePositionInSeconds(), durationInSeconds);
				subtaskTimelines.add(subtaskTimeline);
			}
			previousSubtask = subtask;
		}

		filter = new ItemFilter(true);
		Long durationInSeconds = getDurationInSeconds() - previousSubtask.getRelativePositionInSeconds();
		IdeaFlowTimeline subtaskTimeline = splitTimeline(filter, previousSubtask.getPosition(), end, previousSubtask.getRelativePositionInSeconds(), durationInSeconds);
		subtaskTimelines.add(subtaskTimeline);
		return subtaskTimelines;
	}

	@JsonIgnore
	private IdeaFlowTimeline splitTimeline(ItemFilter filter, LocalDateTime timelineStart, LocalDateTime timelineEnd,
	                                       Long relativeStartInSeconds, Long durationInSeconds) {
		List<Event> eventsBetween = filter.getItemsBetween(events, timelineStart, timelineEnd);
		List<ExecutionEvent> executionEventsBetween = filter.getItemsBetween(executionEvents, timelineStart, timelineEnd);
		List<BlockActivity> blockActivitiesBetween = filter.getItemsBetween(blockActivities, timelineStart, timelineEnd);
		List<ModificationActivity> modificationActivitieBetween = filter.getItemsBetween(modificationActivities, timelineStart, timelineEnd);
		List<IdeaFlowBand> splitIdeaFlowBands = getIdeaFlowBandsBetweenSplitOnOverlap(timelineStart, timelineEnd, relativeStartInSeconds, durationInSeconds);

		return IdeaFlowTimeline.builder()
				.start(timelineStart)
				.end(timelineEnd)
				.events(eventsBetween)
				.executionEvents(executionEventsBetween)
				.blockActivities(blockActivitiesBetween)
				.modificationActivities(modificationActivitieBetween)
				.ideaFlowBands(splitIdeaFlowBands)
				.build();
	}

	private List<IdeaFlowBand> getIdeaFlowBandsBetweenSplitOnOverlap(LocalDateTime timelineStart, LocalDateTime timelineEnd,
	                                                                 Long timelineRelativePositionInSeconds, Long timelineDurationInSeconds) {
		List<IdeaFlowBand> ideaFlowbandsToReturn = new ArrayList<>();
		for (IdeaFlowBand ideaFlowBand : ideaFlowBands) {
			LocalDateTime bandStart = ideaFlowBand.getStart();
			LocalDateTime bandEnd = ideaFlowBand.getEnd();

			boolean startsWithinRange = bandStart.isEqual(timelineStart) || (bandStart.isAfter(timelineStart) && bandStart.isBefore(timelineEnd));
			boolean endsWithinRange = bandEnd.isEqual(timelineEnd) || (bandEnd.isBefore(timelineEnd) && bandEnd.isAfter(timelineStart));

			IdeaFlowBand ideaFlowBandToReturn = null;
			if (startsWithinRange && endsWithinRange) {
				ideaFlowBandToReturn = ideaFlowBand;
			} else {
				if (startsWithinRange) {
					Long ideaFlowBandDuration = timelineDurationInSeconds - (ideaFlowBand.getRelativePositionInSeconds() - timelineRelativePositionInSeconds);
					ideaFlowBandToReturn = createBand(ideaFlowBand.getType(), bandStart, timelineEnd, ideaFlowBand.getRelativePositionInSeconds(), ideaFlowBandDuration);
				} else if (endsWithinRange) {
					Long ideaFlowBandDuration = ideaFlowBand.getDuration() - (timelineRelativePositionInSeconds - ideaFlowBand.getRelativePositionInSeconds());
					ideaFlowBandToReturn = createBand(ideaFlowBand.getType(), timelineStart, bandEnd, timelineRelativePositionInSeconds, ideaFlowBandDuration);
				} else if (timelineStart.isAfter(bandStart) && timelineEnd.isBefore(bandEnd)) {
					ideaFlowBandToReturn = createBand(ideaFlowBand.getType(), timelineStart, timelineEnd, timelineRelativePositionInSeconds, timelineDurationInSeconds);
				}
			}

			if (ideaFlowBandToReturn != null) {
				ideaFlowbandsToReturn.add(ideaFlowBandToReturn);
			}
		}
		return ideaFlowbandsToReturn;
	}

	private IdeaFlowBand createBand(IdeaFlowStateType type, LocalDateTime start, LocalDateTime end, Long relativePositionInSeconds, Long durationInSeconds) {
		return IdeaFlowBand.builder()
				.start(start)
				.end(end)
				.relativePositionInSeconds(relativePositionInSeconds)
				.durationInSeconds(durationInSeconds)
				.type(type)
				.nestedBands(new ArrayList<>())
				.build();
	}

	private static class ItemFilter {

		private boolean includeItemIfPositionEqualsEndTime;

		public ItemFilter(boolean includeItemIfPositionEqualsEndTime) {
			this.includeItemIfPositionEqualsEndTime = includeItemIfPositionEqualsEndTime;
		}

		<T extends Positionable> List<T> getItemsBetween(List<T> items, LocalDateTime start, LocalDateTime end) {
			return items.stream()
					.filter(e -> isOnStartOrBetween(e.getPosition(), start, end))
					.collect(Collectors.toList());
		}

		private boolean isOnStartOrBetween(LocalDateTime position, LocalDateTime start, LocalDateTime end) {
			return position.isEqual(start) || (includeItemIfPositionEqualsEndTime && position.isEqual(end)) ||
					(start.isBefore(position) && end.isAfter(position));
		}

	}

}

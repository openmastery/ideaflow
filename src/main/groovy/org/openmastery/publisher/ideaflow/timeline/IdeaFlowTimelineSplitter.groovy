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

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowSubtaskTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline

public class IdeaFlowTimelineSplitter {

	private IdeaFlowTimeline timeline;

	private Long getDurationInSeconds() {
		timeline.durationInSeconds
	}

	private LocalDateTime getEnd() {
		timeline.end
	}

	private List<Event> getEvents() {
		timeline.events
	}

	private List<ExecutionEvent> getExecutionEvents() {
		timeline.executionEvents
	}

	private List<IdeaFlowBand> getIdeaFlowBands() {
		timeline.ideaFlowBands
	}

	private Long getRelativePositionInSeconds() {
		timeline.relativePositionInSeconds
	}

	private LocalDateTime getStart() {
		timeline.start
	}

	public IdeaFlowTimelineSplitter timeline(IdeaFlowTimeline timeline) {
		this.timeline = timeline
		this
	}

	public List<IdeaFlowSubtaskTimeline> splitBySubtaskEvents() {
		List<Event> subtaskEvents = getSubtaskEvents();
		if (subtaskEvents.isEmpty()) {
			throw new NoSubtaskInTimelineException()
		}

		if (subtaskEvents.size() == 1) {
			LocalDateTime position = subtaskEvents.get(0).getPosition();
			if (position.isEqual(start) || position.isEqual(end)) {
				return [getThisTimelineAsSubtaskTimeline(subtaskEvents.first())];
			}
		}
		splitBySubtask(subtaskEvents);
	}

	private List<Event> getSubtaskEvents() {
		events.findAll { Event event ->
			event.type == EventType.SUBTASK
		}
	}

	private IdeaFlowSubtaskTimeline getThisTimelineAsSubtaskTimeline(Event subtask) {
		IdeaFlowSubtaskTimeline.builder()
				.subtask(subtask)
				.start(start)
				.end(end)
				.durationInSeconds(durationInSeconds)
				.relativePositionInSeconds(relativePositionInSeconds)
				.ideaFlowBands(ideaFlowBands)
				.executionEvents(executionEvents)
				.events(events)
				.build()
	}

	private List<IdeaFlowSubtaskTimeline> splitBySubtask(List<Event> subtaskEvents) {
		Event previousSubtask = null;
		ItemFilter filter = new ItemFilter(false);
		List<IdeaFlowSubtaskTimeline> subtaskTimelines = new ArrayList<IdeaFlowSubtaskTimeline>();

		for (Event subtask : subtaskEvents) {
			if (previousSubtask != null) {
				Long durationInSeconds = subtask.getRelativePositionInSeconds() - previousSubtask.getRelativePositionInSeconds();
				IdeaFlowSubtaskTimeline subtaskTimeline = splitTimeline(filter, previousSubtask.getPosition(), subtask.getPosition(), previousSubtask.getRelativePositionInSeconds(), durationInSeconds);
				subtaskTimelines.add(subtaskTimeline);
			}
			previousSubtask = subtask;
		}

		filter = new ItemFilter(true);
		Long durationInSeconds = durationInSeconds - previousSubtask.getRelativePositionInSeconds();
		IdeaFlowSubtaskTimeline subtaskTimeline = splitTimeline(filter, previousSubtask.getPosition(), end, previousSubtask.getRelativePositionInSeconds(), durationInSeconds);
		subtaskTimelines.add(subtaskTimeline);
		return subtaskTimelines;
	}

	private IdeaFlowSubtaskTimeline splitTimeline(ItemFilter filter, LocalDateTime timelineStart, LocalDateTime timelineEnd,
	                                              Long relativeStartInSeconds, Long durationInSeconds) {
		List<Event> eventsBetween = filter.getItemsBetween(events, timelineStart, timelineEnd);
		List<ExecutionEvent> executionEventsBetween = filter.getItemsBetween(executionEvents, timelineStart, timelineEnd);
		List<IdeaFlowBand> splitIdeaFlowBands = getIdeaFlowBandsBetweenSplitOnOverlap(timelineStart, timelineEnd, relativeStartInSeconds, durationInSeconds);

		return IdeaFlowSubtaskTimeline.builder()
				.start(timelineStart)
				.end(timelineEnd)
				.events(eventsBetween)
				.executionEvents(executionEventsBetween)
				.ideaFlowBands(splitIdeaFlowBands)
				.build();
	}

	private List<IdeaFlowBand> getIdeaFlowBandsBetweenSplitOnOverlap(LocalDateTime timelineStart, LocalDateTime timelineEnd,
	                                                                 Long timelineRelativePositionInSeconds, Long timelineDurationInSeconds) {
		List<IdeaFlowBand> ideaFlowbandsToReturn = new ArrayList<IdeaFlowBand>();
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
				.nestedBands(new ArrayList())
				.build();
	}

	private static class ItemFilter {

		private boolean includeItemIfPositionEqualsEndTime;

		public ItemFilter(boolean includeItemIfPositionEqualsEndTime) {
			this.includeItemIfPositionEqualsEndTime = includeItemIfPositionEqualsEndTime;
		}

		public <T extends Positionable> List<T> getItemsBetween(List<T> items, LocalDateTime start, LocalDateTime end) {
			items.findAll { T item ->
				isOnStartOrBetween(item.getPosition(), start, end)
			}
		}

		private boolean isOnStartOrBetween(LocalDateTime position, LocalDateTime start, LocalDateTime end) {
			return position.isEqual(start) || (includeItemIfPositionEqualsEndTime && position.isEqual(end)) ||
					(start.isBefore(position) && end.isAfter(position));
		}

	}

	private static final class NoSubtaskInTimelineException extends RuntimeException {
	}

}

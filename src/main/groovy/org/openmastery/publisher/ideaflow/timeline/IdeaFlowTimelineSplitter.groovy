/*
 * Copyright 2017 New Iron Group, Inc.
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

import org.openmastery.publisher.api.Interval
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowSubtaskTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.ideaflow.IntervalSplitter

import java.time.LocalDateTime

public class IdeaFlowTimelineSplitter {

	private IdeaFlowTaskTimeline timeline;

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

	public IdeaFlowTimelineSplitter timeline(IdeaFlowTaskTimeline timeline) {
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
				IdeaFlowSubtaskTimeline subtaskTimeline = splitTimeline(filter, previousSubtask, subtask.getPosition(), durationInSeconds);
				subtaskTimelines.add(subtaskTimeline);
			}
			previousSubtask = subtask;
		}

		filter = new ItemFilter(true);
		Long durationInSeconds = durationInSeconds - previousSubtask.getRelativePositionInSeconds();
		IdeaFlowSubtaskTimeline subtaskTimeline = splitTimeline(filter, previousSubtask, end, durationInSeconds);
		subtaskTimelines.add(subtaskTimeline);

		return subtaskTimelines;
	}
	
	private IdeaFlowSubtaskTimeline splitTimeline(ItemFilter filter, Event subtask, LocalDateTime timelineEnd,
	                                              Long durationInSeconds) {
		LocalDateTime timelineStart = subtask.getPosition()
		Long relativeStartInSeconds = subtask.getRelativePositionInSeconds()
		List<Event> eventsBetween = filter.getItemsBetween(events, timelineStart, timelineEnd);
		List<ExecutionEvent> executionEventsBetween = filter.getItemsBetween(executionEvents, timelineStart, timelineEnd);
		List<IdeaFlowBand> splitIdeaFlowBands = new IntervalSplitter<>()
				.start(timelineStart)
				.end(timelineEnd)
				.durationInSeconds(durationInSeconds)
				.relativePositionInSeconds(relativeStartInSeconds)
				.intervals(ideaFlowBands)
				.intervalFactory(new IdeaFlowBandFactory())
				.split()

		return IdeaFlowSubtaskTimeline.builder()
				.subtask(subtask)
				.start(timelineStart)
				.end(timelineEnd)
				.events(eventsBetween)
				.executionEvents(executionEventsBetween)
				.ideaFlowBands(splitIdeaFlowBands)
				.relativePositionInSeconds(relativeStartInSeconds)
				.durationInSeconds(durationInSeconds)
				.build();
	}

	private static final class IdeaFlowBandFactory implements Interval.Factory<IdeaFlowBand> {
		@Override
		IdeaFlowBand create(IdeaFlowBand interval, LocalDateTime start, LocalDateTime end, Long relativePositionInSeconds, Long durationInSeconds) {
			return IdeaFlowBand.builder()
					.start(start)
					.end(end)
					.relativePositionInSeconds(relativePositionInSeconds)
					.durationInSeconds(durationInSeconds)
					.type(interval.type)
					.nestedBands(new ArrayList())
					.build();
		}
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

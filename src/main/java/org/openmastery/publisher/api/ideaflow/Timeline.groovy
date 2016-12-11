package org.openmastery.publisher.api.ideaflow

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor
import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.event.Event

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Timeline {

	private String taskName;
	private String description;
	private String project;

	private LocalDateTime start;
	private LocalDateTime end;

	private Long durationInSeconds;
	private Long relativePositionInSeconds;

	private List<ModificationActivity> modificationActivities;
	private List<ExecutionEvent> executionEvents;
	private List<CalendarEvent> calendarEvents;
	private List<Event> events;

}

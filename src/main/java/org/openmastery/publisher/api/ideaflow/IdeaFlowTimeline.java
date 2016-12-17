package org.openmastery.publisher.api.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.Positionable;
import org.openmastery.publisher.api.activity.ModificationActivity;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.task.Task;

import java.util.List;

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
	private List<ExecutionEvent> executionEvents;
	private List<Event> events;


	@Override
	public LocalDateTime getPosition() {
		return start;
	}

}

package org.openmastery.publisher.api.ideaflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.Positionable;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.ExecutionEvent;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaFlowSubtaskTimeline implements Positionable, IdeaFlowTimeline {

	@JsonIgnore
	private Event subtask;

	private LocalDateTime start;
	private LocalDateTime end;

	private Long durationInSeconds;
	private Long relativePositionInSeconds;

	private List<IdeaFlowBand> ideaFlowBands;
	private List<ExecutionEvent> executionEvents;
	private List<Event> events;

	@JsonIgnore
	@Override
	public LocalDateTime getPosition() {
		return start;
	}

	@JsonIgnore
	public Long getRelativeStart() {
		return relativePositionInSeconds;
	}

	@JsonIgnore
	public Long getRelativeEnd() {
		return relativePositionInSeconds + durationInSeconds;
	}


}

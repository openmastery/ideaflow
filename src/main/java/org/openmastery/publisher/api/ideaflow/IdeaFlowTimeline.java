package org.openmastery.publisher.api.ideaflow;

import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.ExecutionEvent;

import java.util.List;

public interface IdeaFlowTimeline {

	List<IdeaFlowBand> getIdeaFlowBands();

	List<ExecutionEvent> getExecutionEvents();

	List<Event> getEvents();

	Long getDurationInSeconds();

	LocalDateTime getStart();

	LocalDateTime getEnd();

	Long getRelativePositionInSeconds();

}

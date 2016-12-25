package org.openmastery.publisher.api.ideaflow;

import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.ExecutionEvent;

import java.util.List;

public interface IdeaFlowMetricsTimeline {

	List<IdeaFlowBand> getIdeaFlowBands();

	List<ExecutionEvent> getExecutionEvents();

	List<Event> getEvents();

	Long getDurationInSeconds();

}

package org.openmastery.publisher.api.journey;

import lombok.*;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.metrics.TimelineMetrics;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
public class Experiment extends AbstractRelativeInterval {

	Event wtfYayEvent;

	List<String> tags; //derived from WTF/YAY #hashtags

	List<ExecutionCycle> executionCycles;

	TimelineMetrics metrics;

	public Experiment(Event wtfYayEvent, Long durationInSeconds) {
		this.wtfYayEvent = wtfYayEvent;
		this.executionCycles = new ArrayList<ExecutionCycle>();

		setRelativeStart(wtfYayEvent.getRelativePositionInSeconds());
		setDurationInSeconds(durationInSeconds);
	}

	public Long getId() {
		return wtfYayEvent.getId();
	}

	public void addExecutionEvent(ExecutionEvent event) {
		updateEndTimeOfLastExecutionCycle(event.getRelativePositionInSeconds());

		Long durationInSeconds = getRelativeEnd() - event.getRelativePositionInSeconds();
		ExecutionCycle executionCycle = new ExecutionCycle(event, durationInSeconds);
		executionCycles.add(executionCycle);
	}

	void updateEndTimeOfLastExecutionCycle(Long beginningOfNextExecutionCycle) {
		if (executionCycles.size() > 0) {
			ExecutionCycle lastCycle = executionCycles.get(executionCycles.size() - 1);
			lastCycle.setDurationInSeconds(beginningOfNextExecutionCycle - lastCycle.getRelativeStart());
		}
	}
}

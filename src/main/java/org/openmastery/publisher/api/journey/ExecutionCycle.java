package org.openmastery.publisher.api.journey;

import lombok.*;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.ExecutionEvent;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
public class ExecutionCycle extends AbstractRelativeInterval {

	private ExecutionEvent executionEvent;

	public ExecutionCycle(ExecutionEvent executionEvent, Long durationInSeconds) {
		this.executionEvent = executionEvent;

		setRelativeStart(executionEvent.getRelativePositionInSeconds());
		setDurationInSeconds(durationInSeconds);
	}

}

package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.ExecutionEvent;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
public class ExperimentCycle extends AbstractRelativeInterval {

	@JsonIgnore
	private ExecutionEvent executionEvent;

	public ExperimentCycle(ExecutionEvent executionEvent, Long durationInSeconds) {
		this.executionEvent = executionEvent;

		setRelativeStart(executionEvent.getRelativePositionInSeconds());
		setDurationInSeconds(durationInSeconds);
	}

	public Long getId() { return executionEvent.getId(); }

	public LocalDateTime getPosition() { return executionEvent.getPosition(); }

	public String getProcessName() {
		return executionEvent.getProcessName();
	}

	public String getExecutionTaskType() {
		return executionEvent.getExecutionTaskType();
	}

	public boolean isDebug() {
		return executionEvent.isDebug();
	}

	public boolean isFailed() {
		return executionEvent.isFailed();
	}

	public Long getExecutionDurationInSeconds() {
		return executionEvent.getDurationInSeconds();
	}

	public String getDescription() {
		String failString = isFailed()? "Fail" : "Pass";
		return executionEvent.getExecutionTaskType() + " : " + failString + " : " +executionEvent.getProcessName();
	}

}

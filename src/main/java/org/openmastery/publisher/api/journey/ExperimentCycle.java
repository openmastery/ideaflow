package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.metrics.DurationInSeconds;
import org.openmastery.storyweb.api.ExplodableGraphPoint;

import java.util.ArrayList;
import java.util.List;

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

	public ExplodableGraphPoint toGraphPoint() {
		ExplodableGraphPoint graphPoint = new ExplodableGraphPoint();
		graphPoint.setRelativePath("/exec/"+executionEvent.getId());
		graphPoint.setDurationInSeconds(new DurationInSeconds(getDurationInSeconds()));
		graphPoint.setFrequency(1);
		graphPoint.setDescription(getDescription());
		graphPoint.setTypeName(getClass().getSimpleName());
		graphPoint.setPosition(executionEvent.getPosition());

		return graphPoint;
	}
}

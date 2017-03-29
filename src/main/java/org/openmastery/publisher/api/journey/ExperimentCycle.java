package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.storyweb.api.metrics.Metric;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
public class ExperimentCycle extends AbstractRelativeInterval implements StoryElement {

	@JsonIgnore
	private ExecutionEvent executionEvent;

	@JsonIgnore
	String parentPath;

	Set<String> painTags;
	Set<String> contextTags;

	List<Metric<?>> allMetrics;
	List<Metric<?>> dangerMetrics;


	public ExperimentCycle(String parentPath, ExecutionEvent executionEvent, Long durationInSeconds) {
		this.parentPath = parentPath;
		this.executionEvent = executionEvent;
		painTags = new HashSet<String>();
		contextTags = new HashSet<String>();

		setRelativeStart(executionEvent.getRelativePositionInSeconds());
		setDurationInSeconds(durationInSeconds);

		executionEvent.setFullPath(getFullPath());
	}

	@JsonIgnore
	public Long getId() {
		return executionEvent.getId();
	}

	public String getRelativePath() {
		return "/experiment/" + executionEvent.getId();
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
		executionEvent.setFullPath(getFullPath());
	}

	@JsonIgnore
	public String getFullPath() {
		return parentPath + getRelativePath();
	}

	public LocalDateTime getPosition() {
		return executionEvent.getPosition();
	}

	public Long getRelativePositionInSeconds() {
		return executionEvent.getRelativePositionInSeconds();
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

	@JsonIgnore
	public int getFrequency() {
		return 1;
	}

	@JsonIgnore
	public String getDescription() {
		String failString = isFailed() ? "Fail" : "Pass";
		return executionEvent.getExecutionTaskType() + " : " + failString + " : " + executionEvent.getProcessName();
	}

	@JsonIgnore
	@Override
	public List<? extends StoryElement> getChildStoryElements() {
		return Collections.emptyList();
	}

}

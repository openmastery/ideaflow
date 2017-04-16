package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.EventType;
import org.openmastery.publisher.api.metrics.CapacityDistribution;
import org.openmastery.storyweb.api.metrics.Metric;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProgressTick extends AbstractRelativeInterval implements StoryContextElement {

	@JsonIgnore
	Event event;

	@JsonIgnore
	String parentPath;

	CapacityDistribution capacityDistribution;

	Set<String> painTags;
	Set<String> contextTags;

	List<Metric<?>> allMetrics;
	List<Metric<?>> dangerMetrics;


	public ProgressTick(String parentPath, Event progressEvent) {
		this.event = progressEvent;
		this.parentPath = parentPath;
		setRelativeStart(progressEvent.getRelativePositionInSeconds());

		contextTags = TagsUtil.extractUniqueHashTags(progressEvent.getDescription());
		painTags = new HashSet<>();

		//TODO fix this hack properly in IdeaFlowStoryGenerator... overwriting subtask for default milestone
		if (event.getType() == EventType.NOTE) {
			event.setFullPath(getFullPath());
		}
	}

	@JsonIgnore
	public Long getId() {
		return event.getId();
	}

	public String getRelativePath() {
		return "/milestone/" + event.getId();
	}

	public String getFullPath() {
		return parentPath + getRelativePath();
	}

	public LocalDateTime getPosition() {
		return event.getPosition();
	}

	public String getDescription() {
		return event.getDescription();
	}

	@JsonIgnore
	public LocalDateTime getStart() {
		return event.getPosition();
	}


	@JsonIgnore
	@Override
	public int getFrequency() {
		return 1;
	}

	@JsonIgnore
	@Override
	public List<? extends StoryElement> getChildStoryElements() {
		return Collections.emptyList();
	}


}

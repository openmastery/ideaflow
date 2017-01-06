package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.storyweb.api.metrics.Metric;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
public class DiscoveryCycle extends AbstractRelativeInterval implements StoryElement {


	@JsonIgnore
	String parentPath;
	@JsonIgnore
	Event event;

	Set<String> painTags;
	Set<String> contextTags;

	String faqAnnotation;
	FormattableSnippet formattableSnippet;

	List<ExperimentCycle> experimentCycles;

	List<Metric<?>> metrics;

	public DiscoveryCycle(String parentPath, Event wtfYayEvent, Long durationInSeconds) {
		this.parentPath = parentPath;
		this.event = wtfYayEvent;
		this.experimentCycles = new ArrayList<ExperimentCycle>();
		this.contextTags = new HashSet<String>();
		this.painTags = TagsUtil.extractUniqueHashTags(wtfYayEvent.getComment());

		setRelativeStart(wtfYayEvent.getRelativePositionInSeconds());
		setDurationInSeconds(durationInSeconds);

		event.setFullPath(getFullPath());
	}

	@JsonIgnore
	public Long getId() { return event.getId(); }

	public String getRelativePath() {
		return "/discovery/"+event.getId();
	}

	@JsonIgnore
	public String getFullPath() { return parentPath + getRelativePath(); }

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
		event.setFullPath(getFullPath());

		for (ExperimentCycle experimentCycle : experimentCycles) {
			experimentCycle.setParentPath(getFullPath());
		}
	}

	public LocalDateTime getPosition() { return event.getPosition(); }

	public Long getRelativePositionInSeconds() {
		return event.getRelativePositionInSeconds();
	}

	public String getDescription() {
		return event.getComment();
	}

	public void addExperimentCycle(ExperimentCycle experimentCycle) {
		experimentCycles.add(experimentCycle);
	}

	public void addFaq(String faqComment) {
		this.faqAnnotation = faqComment;
		contextTags = TagsUtil.extractUniqueHashTags(faqComment);
	}

	void updateEndTimeOfLastExperimentCycle(Long beginningOfNextExecutionCycle) {
		if (experimentCycles.size() > 0) {
			ExperimentCycle lastCycle = experimentCycles.get(experimentCycles.size() - 1);
			lastCycle.setDurationInSeconds(beginningOfNextExecutionCycle - lastCycle.getRelativeStart());
		}
	}

	@JsonIgnore
	public int getFrequency() { return getExperimentCycles().size(); }

	@JsonIgnore
	@Override
	public List<? extends StoryElement> getChildStoryElements() {
		return experimentCycles;
	}


}

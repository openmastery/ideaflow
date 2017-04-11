package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.ideaflow.Haystack;
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline;
import org.openmastery.publisher.api.metrics.CapacityDistribution;
import org.openmastery.storyweb.api.metrics.Metric;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubtaskStory implements StoryContextElement {

	@JsonIgnore
	Event subtaskEvent;

	@JsonIgnore
	IdeaFlowTimeline timeline;

	@JsonIgnore
	String parentPath;

	CapacityDistribution capacityDistribution;

	Set<String> painTags;
	Set<String> contextTags;

	List<Metric<?>> allMetrics;
	List<Metric<?>> dangerMetrics;

	List<ProgressTick> progressTicks;


	List<Haystack> haystacks;
	List<TroubleshootingJourney> troubleshootingJourneys;

	public SubtaskStory(String parentPath, Event subtask, IdeaFlowTimeline timeline) {
		this.subtaskEvent = subtask;
		this.timeline = timeline;
		this.parentPath = parentPath;
		this.progressTicks = new ArrayList<ProgressTick>();
		this.troubleshootingJourneys = new ArrayList<TroubleshootingJourney>();

		contextTags = TagsUtil.extractUniqueHashTags(subtaskEvent.getDescription());
		painTags = new HashSet<String>();

		subtaskEvent.setFullPath(getFullPath());
	}

	@Override
	public String getRelativePath() {
		return "/subtask/"+getId();
	}

	public String getFullPath() { return parentPath + getRelativePath(); }

	@Override
	public String getDescription() {
		return subtaskEvent.getDescription();
	}

	@Override
	public Long getRelativePositionInSeconds() {
		return timeline.getRelativePositionInSeconds();
	}

	@Override
	public Long getDurationInSeconds() {
		return timeline.getDurationInSeconds();
	}

	@Override
	public LocalDateTime getPosition() {
		return timeline.getStart();
	}

	@JsonIgnore
	public Long getTaskId() { return subtaskEvent.getTaskId(); }

	@JsonIgnore
	public Long getId() {
		return subtaskEvent.getId();
	}

	@JsonIgnore
	@Override
	public int getFrequency() { return progressTicks.size(); }

	@JsonIgnore
	@Override
	public List<? extends StoryElement> getChildStoryElements() {
		return progressTicks;
	}



}

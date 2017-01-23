package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline;
import org.openmastery.publisher.api.metrics.CapacityDistribution;
import org.openmastery.storyweb.api.metrics.Metric;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"subtaskEvent", "timeline", "parentPath"})
public class SubtaskStory implements StoryContextElement {

	Event subtaskEvent;
	IdeaFlowTimeline timeline;
	String parentPath;

	CapacityDistribution capacityDistribution;

	Set<String> painTags;
	Set<String> contextTags;

	List<ProgressMilestone> milestones;

	List<TroubleshootingJourney> troubleshootingJourneys;

	List<Metric<?>> metrics;

	public SubtaskStory(String parentPath, Event subtask, IdeaFlowTimeline timeline) {
		this.subtaskEvent = subtask;
		this.timeline = timeline;
		this.parentPath = parentPath;
		this.milestones = new ArrayList<ProgressMilestone>();
		this.troubleshootingJourneys = new ArrayList<TroubleshootingJourney>();

		contextTags = TagsUtil.extractUniqueHashTags(subtaskEvent.getComment());
		painTags = new HashSet<String>();

		subtaskEvent.setFullPath(getFullPath());
	}

	@Override
	public String getRelativePath() {
		return "/subtask/"+getId();
	}

	@JsonIgnore
	public String getFullPath() { return parentPath + getRelativePath(); }

	@Override
	public String getDescription() {
		return subtaskEvent.getComment();
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

	@Override
	public int getFrequency() { return milestones.size(); }

	@JsonIgnore
	@Override
	public List<? extends StoryElement> getChildStoryElements() {
		return milestones;
	}



}

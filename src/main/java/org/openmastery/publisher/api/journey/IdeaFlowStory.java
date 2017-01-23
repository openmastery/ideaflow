package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline;
import org.openmastery.publisher.api.metrics.CapacityDistribution;
import org.openmastery.publisher.api.task.Task;
import org.openmastery.storyweb.api.metrics.Metric;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonIgnoreProperties({"task", "timeline"})
public class IdeaFlowStory implements StoryContextElement {

	Task task;
	IdeaFlowTimeline timeline;

	CapacityDistribution capacityDistribution;
	List<SubtaskStory> subtasks;

	Set<String> painTags;
	Set<String> contextTags;

	List<Metric<?>> metrics;

	public IdeaFlowStory() {
		subtasks = new ArrayList<SubtaskStory>();
	}

	public IdeaFlowStory(Task task, IdeaFlowTimeline timeline) {
		this.task = task;
		this.timeline = timeline;

		contextTags = TagsUtil.extractUniqueHashTags(task.getDescription());
		painTags = new HashSet<String>();
	}

	@Override
	public String getRelativePath() {
		return "/task/"+task.getId();
	}

	@JsonIgnore
	public String getFullPath() {
		return getRelativePath();
	}

	@Override
	public String getDescription() {
		return task.getName() + " : " + task.getDescription();
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

	@Override
	public int getFrequency() { return subtasks.size(); }


	@JsonIgnore
	public Long getId() {
		return task.getId();
	}

	@JsonIgnore
	@Override
	public List<? extends StoryElement> getChildStoryElements() {
		return subtasks;
	}


}

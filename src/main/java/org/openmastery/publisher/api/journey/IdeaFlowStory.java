package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline;
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline;
import org.openmastery.publisher.api.metrics.CapacityDistribution;
import org.openmastery.publisher.api.task.Task;
import org.openmastery.storyweb.api.metrics.Metric;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
public class IdeaFlowStory implements StoryContextElement {

	@JsonIgnore
	Task task;

	@JsonIgnore
	IdeaFlowTimeline timeline;

	CapacityDistribution capacityDistribution;
	List<SubtaskStory> subtasks;

	Set<String> painTags;
	Set<String> contextTags;

	List<Metric<?>> allMetrics;
	List<Metric<?>> dangerMetrics;


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
		return "/task/id/"+task.getId();
	}

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

	@JsonIgnore
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

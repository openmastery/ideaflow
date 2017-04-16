package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline;
import org.openmastery.publisher.api.metrics.CapacityDistribution;
import org.openmastery.publisher.api.task.Task;
import org.openmastery.storyweb.api.metrics.Metric;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		subtasks = new ArrayList<>();
	}

	public IdeaFlowStory(Task task, IdeaFlowTimeline timeline) {
		this.task = task;
		this.timeline = timeline;

		contextTags = TagsUtil.extractUniqueHashTags(task.getDescription());
		painTags = new HashSet<>();
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

	public LocalDateTime getStart() {
		return timeline.getStart();
	}
	public LocalDateTime getEnd() {
		return timeline.getEnd();
	}

	@Override
	public Long getRelativePositionInSeconds() {
		return timeline.getRelativePositionInSeconds();
	}

	@Override
	public Long getDurationInSeconds() {
		return timeline.getDurationInSeconds();
	}


	@JsonIgnore
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

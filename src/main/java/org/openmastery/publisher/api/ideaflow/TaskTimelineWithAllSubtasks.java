package org.openmastery.publisher.api.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.journey.IdeaFlowStory;
import org.openmastery.publisher.api.task.Task;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTimelineWithAllSubtasks {

	private Task task;

	private IdeaFlowTaskTimeline timeline;
	private List<IdeaFlowSubtaskTimeline> subtaskTimelines;
	private List<Haystack> haystacks;

	private IdeaFlowStory ideaFlowStory;

}

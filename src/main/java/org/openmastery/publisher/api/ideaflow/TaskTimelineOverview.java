package org.openmastery.publisher.api.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.journey.IdeaFlowStory;
import org.openmastery.publisher.api.task.Task;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTimelineOverview {

	private Task task;

	private IdeaFlowTaskTimeline timeline;
	private IdeaFlowStory ideaFlowStory;

}

package org.openmastery.publisher.api.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.metrics.SubtaskMetrics;
import org.openmastery.publisher.api.task.Task;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTimelineOverview {

	private Task task;
	private IdeaFlowTaskTimeline timeline;
	private List<SubtaskMetrics> subtaskTimelineMetrics;

}

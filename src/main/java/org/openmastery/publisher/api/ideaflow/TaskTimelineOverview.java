package org.openmastery.publisher.api.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.journey.IdeaFlowStory;
import org.openmastery.publisher.api.journey.StoryElement;
import org.openmastery.publisher.api.journey.SubtaskStory;
import org.openmastery.publisher.api.metrics.SubtaskOverview;
import org.openmastery.publisher.api.task.Task;
import org.openmastery.storyweb.api.metrics.Metric;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTimelineOverview {

	private Task task;

	private IdeaFlowTaskTimeline timeline;
	private IdeaFlowStory ideaFlowStory;

}

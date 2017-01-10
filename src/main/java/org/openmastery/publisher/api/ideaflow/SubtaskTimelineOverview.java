package org.openmastery.publisher.api.ideaflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.journey.IdeaFlowStory;
import org.openmastery.publisher.api.journey.ProgressMilestone;
import org.openmastery.publisher.api.journey.SubtaskStory;
import org.openmastery.publisher.api.journey.TroubleshootingJourney;
import org.openmastery.publisher.api.metrics.SubtaskOverview;
import org.openmastery.storyweb.api.metrics.Metric;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubtaskTimelineOverview {

	@JsonIgnore
	private Event subtask;

	private IdeaFlowSubtaskTimeline timeline;
	IdeaFlowStory ideaFlowStory;


}

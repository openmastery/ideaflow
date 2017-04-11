package org.openmastery.publisher.api.ideaflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.journey.IdeaFlowStory;

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

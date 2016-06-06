package org.ideaflow.publisher.api.timeline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ideaflow.publisher.api.event.Event;
import org.ideaflow.publisher.api.ideaflow.IdeaFlowBand;
import org.ideaflow.publisher.core.timeline.BandTimelineSegment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BandTimeline {

	private String description;
	private Long relativeStart;
	public LocalDateTime start;
	public LocalDateTime end;
	private Duration duration;
	private List<IdeaFlowBand> ideaFlowBands;
	private List<TimeBandGroup> timeBandGroups;
	private List<Event> events;

}

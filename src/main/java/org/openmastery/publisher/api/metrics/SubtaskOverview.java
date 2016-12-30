package org.openmastery.publisher.api.metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubtaskOverview {

	Event subtaskEvent;
	Long durationInSeconds;

	List<Metric<?>> metrics;

	CapacityDistribution capacityDistribution;

	public Long getSubtaskId() {
		return subtaskEvent.getId();
	}

	public String getDescription() {
		return subtaskEvent.getComment();
	}

	public LocalDateTime getStart() { return subtaskEvent.getPosition(); }

}

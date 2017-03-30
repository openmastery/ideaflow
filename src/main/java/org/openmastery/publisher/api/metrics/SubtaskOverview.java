package org.openmastery.publisher.api.metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.RelativePositionable;
import org.openmastery.publisher.api.event.Event;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubtaskOverview implements RelativePositionable {

	@JsonIgnore
	Event subtaskEvent;

	public Long getId() {
		return subtaskEvent.getId();
	}


	public String getRelativePath() {
		return "/subtask/"+subtaskEvent.getId();
	}

	public LocalDateTime getPosition() {
		return subtaskEvent.getPosition();
	}

	public String getDescription() {
		return subtaskEvent.getDescription();
	}

	public Long getRelativePositionInSeconds() {
		return subtaskEvent.getRelativePositionInSeconds();
	}

	Long durationInSeconds;

	@JsonIgnore
	public LocalDateTime getStart() { return subtaskEvent.getPosition(); }


	CapacityDistribution capacityDistribution;

}

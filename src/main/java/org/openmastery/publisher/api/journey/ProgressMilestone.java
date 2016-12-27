package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
public class ProgressMilestone extends AbstractRelativeInterval {

	Event progressEvent;

	public ProgressMilestone(Event progressEvent) {
		this.progressEvent = progressEvent;
		setRelativeStart(progressEvent.getRelativePositionInSeconds());
	}

	public LocalDateTime getStart() {
		return progressEvent.getPosition();
	}

	public String getDescription() {
		return progressEvent.getComment();
	}



}

package org.openmastery.publisher.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractPositionable implements Positionable, RelativePositionable {

	@JsonIgnore
	private Long taskId;

	private LocalDateTime position;
	private Long relativePositionInSeconds;


	// simplify dozer mapping

	@JsonIgnore
	public LocalDateTime getStart() {
		return position;
	}

	@JsonIgnore
	public void setStart(LocalDateTime position) {
		this.position = position;
	}
}

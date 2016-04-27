package org.ideaflow.publisher.core.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdleActivityEntity {

	private long id;
	private long taskId;

	private LocalDateTime start;
	private LocalDateTime end;

	private String comment;

	private boolean auto;

	public Duration getDuration() {
		return Duration.between(start, end);
	}

}

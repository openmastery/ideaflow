package org.ideaflow.publisher.api;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

	private long id;
	private long taskId;

	private LocalDateTime position;

	private String comment;
	private EventType eventType;

}

package org.ideaflow.publisher.core.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ideaflow.publisher.api.event.EventType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {

	private long id;
	private long taskId;

	private LocalDateTime position;

	private String comment;
	private EventType eventType;

}

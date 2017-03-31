package org.openmastery.publisher.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnotatedEvent {

	private Long eventId;
	private Long taskId;
	private String fullPath;

	private String description;
	private String faq;

	private EventType type;

	public void fromEvent(Event event) {
		this.eventId = event.getId();
		this.taskId = event.getTaskId();
		this.description = event.getDescription();
		this.type = event.getType();
		this.fullPath = event.getFullPath();
	}
}

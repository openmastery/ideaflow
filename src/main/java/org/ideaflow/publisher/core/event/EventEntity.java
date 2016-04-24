package org.ideaflow.publisher.core.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {

	private long id;
	private long taskId;

	private String message;
	private Type eventType;


	public enum Type {
		NOTE,
		SUBTASK
	}
}

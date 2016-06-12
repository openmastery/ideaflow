package org.openmastery.publisher.api.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

	private Long id;
	private Long taskId;

	private LocalDateTime position;

	private String comment;
	private EventType type;

	private Long relativePositionInSeconds;
}

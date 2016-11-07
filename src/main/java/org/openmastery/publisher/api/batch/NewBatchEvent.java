package org.openmastery.publisher.api.batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.event.EventType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewBatchEvent {

	private Long taskId;
	private String comment;
	private EventType type;

	private LocalDateTime endTime;

}

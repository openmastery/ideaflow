package org.openmastery.publisher.api.batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.event.EventType;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewBatchEvent implements BatchItem {

	private Long taskId;
	private String comment;
	private EventType type;

	private LocalDateTime position;

}

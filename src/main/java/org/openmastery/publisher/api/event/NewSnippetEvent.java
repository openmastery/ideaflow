package org.openmastery.publisher.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.batch.BatchItem;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewSnippetEvent implements BatchItem {

	private Long taskId;
	private String comment;

	private EventType eventType;
	private LocalDateTime position;

	private String source;
	private String snippet;
}

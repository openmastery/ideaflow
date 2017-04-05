package org.openmastery.publisher.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.batch.BatchItem;

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

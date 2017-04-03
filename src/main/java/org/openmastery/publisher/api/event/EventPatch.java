package org.openmastery.publisher.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.journey.FormattableSnippet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventPatch {

	private String description;
	private String faq;

	private FormattableSnippet formattableSnippet;
}

package org.openmastery.storyweb.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqSummary {
	Set<String> tags;
	String eventComment;
	String faqComment;


	//TODO make this a hateos link
	Long taskId;
	Long eventId;

	LocalDateTime position;
}

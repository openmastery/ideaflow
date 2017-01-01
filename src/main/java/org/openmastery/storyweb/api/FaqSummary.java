package org.openmastery.storyweb.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqSummary {
	List<String> tags;
	String eventComment;
	String faqComment;


	//TODO make this a hateos link
	Long taskId;
	Long eventId;

	LocalDateTime position;
}

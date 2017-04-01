package org.openmastery.storyweb.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.event.EventType;

import java.util.Set;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryPoint {

	String taskPath;
	String fullPath;

	String faqType;
	String eventDescription;
	String faqAnnotation;
	String taskName;
	LocalDateTime position;

	Long journeyPainInSeconds;
	Long taskPainInSeconds;

	Set<String> contextTags;
	Set<String> painTags;
}

//storyweb

//task/id/subtask/5/pain/1 flatten with these coordinates
//faq is optional
//context tags pushed from task/subtask context
//painTags are extracted from the specific example

//metrics are inherited from parent context, task, and journey metrics, associated with FAQ


// , task, task
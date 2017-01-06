package org.openmastery.publisher.api.journey;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.joda.time.LocalDateTime;

import java.util.List;
import java.util.Set;

@JsonPropertyOrder({ "relativePath", "description", "relativePositionInSeconds", "durationInSeconds", "position", "contextTags", "painTags", "frequency" })
public interface StoryElement {

	String getRelativePath();
	String getDescription();
	Long getRelativePositionInSeconds();
	Long getDurationInSeconds();

	LocalDateTime getPosition();

	Set<String> getPainTags();
	Set<String> getContextTags();

	int getFrequency();

	List<? extends StoryElement> getChildStoryElements();


}

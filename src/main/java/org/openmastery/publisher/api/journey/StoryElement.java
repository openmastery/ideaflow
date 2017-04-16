package org.openmastery.publisher.api.journey;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.openmastery.storyweb.api.metrics.Metric;

import java.time.LocalDateTime;
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

	List<Metric<?>> getAllMetrics();
	List<Metric<?>> getDangerMetrics();

	int getFrequency();

	List<? extends StoryElement> getChildStoryElements();


}

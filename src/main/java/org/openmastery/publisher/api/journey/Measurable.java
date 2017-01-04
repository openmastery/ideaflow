package org.openmastery.publisher.api.journey;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.Positionable;
import org.openmastery.publisher.api.metrics.DurationInSeconds;
import org.openmastery.publisher.api.metrics.GraphPoint;

import java.util.List;
import java.util.Set;

public interface Measurable {

	Long getId();
	Set<String> getPainTags();
	Set<String> getContextTags();

	LocalDateTime getPosition();
	Long getRelativePositionInSeconds();
	Long getDurationInSeconds();
	int getFrequency();

}

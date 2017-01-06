package org.openmastery.publisher.api.journey;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.openmastery.publisher.api.metrics.CapacityDistribution;

@JsonPropertyOrder({ "relativePath", "description", "relativePositionInSeconds", "durationInSeconds", "position", "contextTags", "painTags", "frequency", "capacityDistribution" })
public interface StoryContextElement extends StoryElement {

	CapacityDistribution getCapacityDistribution();

	void setCapacityDistribution(CapacityDistribution capacityDistribution);

}

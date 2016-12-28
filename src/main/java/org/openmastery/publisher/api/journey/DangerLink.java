package org.openmastery.publisher.api.journey;

import lombok.*;
import org.openmastery.publisher.api.RelativePositionable;
import org.openmastery.publisher.api.metrics.Metric;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DangerLink implements RelativePositionable {

	Long eventId;
	Long relativePositionInSeconds;
	Metric<?> metric;
}

package org.openmastery.publisher.api.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.Positionable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractPositionable implements Positionable {

	private Long taskId;
	private LocalDateTime position;
	private Long relativePositionInSeconds;

}

package org.openmastery.publisher.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PositionableImpl implements Positionable {

	private LocalDateTime position;
	private Long relativePositionInSeconds;

}

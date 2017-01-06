package org.openmastery.publisher.api.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.openmastery.publisher.api.AbstractPositionable;
import org.openmastery.publisher.api.RelativeInterval;

import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
public class ExecutionEvent extends AbstractPositionable {

	private String fullPath;

	@JsonIgnore
	private Long id;

	private String processName;
	private String executionTaskType;

	private boolean debug;
	private boolean failed;

	private Long durationInSeconds;

}

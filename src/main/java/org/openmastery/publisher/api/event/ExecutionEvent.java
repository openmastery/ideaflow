package org.openmastery.publisher.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.openmastery.publisher.api.activity.AbstractPositionable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ExecutionEvent extends AbstractPositionable {

	private String processName;
	private String executionType;

	private boolean debug;
	private boolean failed;

}

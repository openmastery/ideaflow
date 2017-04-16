package org.openmastery.publisher.api.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewExecutionActivity implements NewActivity {

	private Long taskId;
	private Long durationInSeconds;
	private String processName;
	private int exitCode;
	private String executionTaskType;
	private boolean isDebug;

	@NotNull
	private LocalDateTime endTime;

}

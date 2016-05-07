package org.ideaflow.publisher.api.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

	private Long taskId;
	private String taskName;
	private String projectName;

}

package org.ideaflow.publisher.api;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaFlowState {

	private IdeaFlowStateType type;

	private String taskId;

	private LocalDateTime start;
	private LocalDateTime end;

	private String startingComment;
	private String endingComment;

}

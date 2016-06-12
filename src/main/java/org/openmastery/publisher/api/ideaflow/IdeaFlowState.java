package org.openmastery.publisher.api.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaFlowState implements Comparable<IdeaFlowState> {

	private Long taskId;

	private IdeaFlowStateType type;

	private LocalDateTime start;
	private LocalDateTime end;

	private String startingComment;
	private String endingComment;

	private boolean isLinkedToPrevious;
	private boolean isNested;

	public boolean isOfType(IdeaFlowStateType... typesToCheck) {
		for (IdeaFlowStateType typeToCheck : typesToCheck) {
			if (typeToCheck == type) {
				return true;
			}
		}
		return false;
	}

	public static IdeaFlowStateBuilder from(IdeaFlowState state) {
		return builder().type(state.getType())
				.start(state.getStart())
				.end(state.getEnd())
				.startingComment(state.getStartingComment())
				.endingComment(state.getEndingComment())
				.isLinkedToPrevious(state.isLinkedToPrevious())
				.isNested(state.isNested());
	}

	@Override
	public int compareTo(IdeaFlowState o) {
		return start.compareTo(o.start);
	}

}

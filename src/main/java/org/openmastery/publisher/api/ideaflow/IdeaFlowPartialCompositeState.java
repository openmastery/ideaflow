package org.openmastery.publisher.api.ideaflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaFlowPartialCompositeState {

	private IdeaFlowState activeState;
	private IdeaFlowState containingState;

	@JsonIgnore
	public boolean isBandStartAllowed(IdeaFlowStateType type) {
		boolean isActiveStateConflict = (activeState != null) && activeState.isConflict();
		boolean isContainingStateOfType = (containingState != null) && (containingState.isOfType(type) == false);
		return (isActiveStateConflict && isContainingStateOfType) == false;
	}

	@JsonIgnore
	public boolean isBandEndAllowed(IdeaFlowStateType type) {
		throw new UnsupportedOperationException();
	}

	@JsonIgnore
	public boolean isInState(IdeaFlowStateType type) {
		return isStateOfType(activeState, type) || isStateOfType(containingState, type);
	}

	public IdeaFlowState getActiveConflict() {
		return isStateOfType(activeState, IdeaFlowStateType.TROUBLESHOOTING) ? activeState : null;
	}

	public IdeaFlowState getActiveLearningOrRework() {
		IdeaFlowState activeLearningOrRework = null;
		if (isStateOfType(containingState, IdeaFlowStateType.LEARNING, IdeaFlowStateType.REWORK)) {
			activeLearningOrRework = containingState;
		} else if (isStateOfType(activeState, IdeaFlowStateType.LEARNING, IdeaFlowStateType.REWORK)) {
			activeLearningOrRework = activeState;
		}
		return activeLearningOrRework;
	}

	private boolean isStateOfType(IdeaFlowState state, IdeaFlowStateType ... types) {
		return (state != null) && state.isOfType(types);
	}

}

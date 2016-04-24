package org.ideaflow.publisher.core.ideaflow;

import org.ideaflow.publisher.api.IdeaFlowState;

import java.util.ArrayList;
import java.util.List;

public class IdeaFlowInMemoryPersistenceService implements IdeaFlowPersistenceService {

	private IdeaFlowState activeState;
	private IdeaFlowState containingState;
	private List<IdeaFlowState> stateList = new ArrayList<>();

	@Override
	public IdeaFlowState getActiveState() {
		return activeState;
	}

	@Override
	public IdeaFlowState getContainingState() {
		return containingState;
	}

	public List<IdeaFlowState> getStateList() {
		return stateList;
	}

	@Override
	public void saveActiveState(IdeaFlowState activeState) {
		saveActiveState(activeState, null);
	}

	@Override
	public void saveActiveState(IdeaFlowState activeState, IdeaFlowState containingState) {
		this.activeState = activeState;
		this.containingState = containingState;
	}

	@Override
	public void saveTransition(IdeaFlowState stateToSave, IdeaFlowState activeState) {
		stateList.add(stateToSave);
		saveActiveState(activeState);
	}

}

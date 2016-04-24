package org.ideaflow.publisher.core.ideaflow;

import java.util.ArrayList;
import java.util.List;

public class IdeaFlowInMemoryPersistenceService implements IdeaFlowPersistenceService {

	private IdeaFlowStateEntity activeState;
	private IdeaFlowStateEntity containingState;
	private List<IdeaFlowStateEntity> stateList = new ArrayList<>();

	@Override
	public IdeaFlowStateEntity getActiveState() {
		return activeState;
	}

	@Override
	public IdeaFlowStateEntity getContainingState() {
		return containingState;
	}

	public List<IdeaFlowStateEntity> getStateList() {
		return stateList;
	}

	@Override
	public void saveActiveState(IdeaFlowStateEntity activeState) {
		saveActiveState(activeState, null);
	}

	@Override
	public void saveActiveState(IdeaFlowStateEntity activeState, IdeaFlowStateEntity containingState) {
		this.activeState = activeState;
		this.containingState = containingState;
	}

	@Override
	public void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowStateEntity activeState) {
		stateList.add(stateToSave);
		saveActiveState(activeState);
	}

}

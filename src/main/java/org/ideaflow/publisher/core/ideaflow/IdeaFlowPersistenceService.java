package org.ideaflow.publisher.core.ideaflow;

import org.ideaflow.publisher.api.IdeaFlowState;

public interface IdeaFlowPersistenceService {

	IdeaFlowState getActiveState();

	IdeaFlowState getContainingState();

	void saveActiveState(IdeaFlowState activeState);

	void saveActiveState(IdeaFlowState activeState, IdeaFlowState containingState);

	void saveTransition(IdeaFlowState stateToSave, IdeaFlowState activeState);

}

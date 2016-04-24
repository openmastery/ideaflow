package org.ideaflow.publisher.core.ideaflow;

public interface IdeaFlowPersistenceService {

	IdeaFlowStateEntity getActiveState();

	IdeaFlowStateEntity getContainingState();

	void saveActiveState(IdeaFlowStateEntity activeState);

	void saveActiveState(IdeaFlowStateEntity activeState, IdeaFlowStateEntity containingState);

	void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowStateEntity activeState);

}

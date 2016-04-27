package org.ideaflow.publisher.core.ideaflow;

import org.ideaflow.publisher.core.activity.IdleActivityEntity;
import org.ideaflow.publisher.core.event.EventEntity;

public interface IdeaFlowPersistenceService {

	IdeaFlowStateEntity getActiveState();

	IdeaFlowStateEntity getContainingState();

	void saveActiveState(IdeaFlowStateEntity activeState);

	void saveActiveState(IdeaFlowStateEntity activeState, IdeaFlowStateEntity containingState);

	void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowStateEntity activeState);


	void saveIdleActivity(IdleActivityEntity idleActivity);

	void saveEvent(EventEntity event);

}

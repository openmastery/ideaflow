package org.ideaflow.publisher.core.ideaflow;

import org.ideaflow.publisher.core.activity.EditorActivityEntity;
import org.ideaflow.publisher.core.activity.IdleTimeBandEntity;
import org.ideaflow.publisher.core.event.EventEntity;

import java.util.List;

public interface IdeaFlowPersistenceService {

	IdeaFlowStateEntity getActiveState(long taskId);

	IdeaFlowStateEntity getContainingState(long taskId);

	List<IdeaFlowStateEntity> getStateList(long taskId);

	List<IdleTimeBandEntity> getIdleTimeBandList(long taskId);

	List<EventEntity> getEventList(long taskId);

	List<EditorActivityEntity> getEditorActivityList(long taskId);

	void saveActiveState(IdeaFlowStateEntity activeState);

	void saveActiveState(IdeaFlowStateEntity activeState, IdeaFlowStateEntity containingState);

	void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowStateEntity activeState);


	void saveIdleActivity(IdleTimeBandEntity idleActivity);

	void saveEvent(EventEntity event);

	void saveEditorActivity(EditorActivityEntity activity);

}

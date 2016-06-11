package org.openmastery.publisher.core;

import org.openmastery.publisher.core.activity.EditorActivityEntity;
import org.openmastery.publisher.core.activity.IdleTimeBandEntity;
import org.openmastery.publisher.core.event.EventEntity;
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity;
import org.openmastery.publisher.core.task.TaskEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface IdeaFlowPersistenceService {

	IdeaFlowStateEntity getActiveState(long taskId);

	IdeaFlowStateEntity getContainingState(long taskId);

	List<IdeaFlowStateEntity> getStateList(long taskId);

	List<IdleTimeBandEntity> getIdleTimeBandList(long taskId);

	List<EventEntity> getEventList(long taskId);

	List<EditorActivityEntity> getEditorActivityList(long taskId);

	LocalDateTime getMostRecentActivityEnd(long taskId);

	void saveActiveState(IdeaFlowStateEntity activeState);

	void saveActiveState(IdeaFlowStateEntity activeState, IdeaFlowStateEntity containingState);

	void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowStateEntity activeState);


	IdleTimeBandEntity saveIdleActivity(IdleTimeBandEntity idleActivity);

	EventEntity saveEvent(EventEntity event);

	EditorActivityEntity saveEditorActivity(EditorActivityEntity activity);

	TaskEntity saveTask(TaskEntity task);

	TaskEntity findTaskWithId(long taskId);

	TaskEntity findTaskWithName(String taskName);

}

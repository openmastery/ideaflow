package org.openmastery.publisher.core;

import org.openmastery.publisher.core.activity.ActivityEntity;
import org.openmastery.publisher.core.activity.EditorActivityEntity;
import org.openmastery.publisher.core.activity.ExternalActivityEntity;
import org.openmastery.publisher.core.activity.IdleActivityEntity;
import org.openmastery.publisher.core.event.EventEntity;
import org.openmastery.publisher.core.ideaflow.IdeaFlowPartialStateEntity;
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity;
import org.openmastery.publisher.core.task.TaskEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface IdeaFlowPersistenceService {

	IdeaFlowPartialStateEntity getActiveState(long taskId);

	IdeaFlowPartialStateEntity getContainingState(long taskId);

	List<IdeaFlowStateEntity> getStateList(long taskId);

	List<ActivityEntity> getActivityList(long taskId);

	List<IdleActivityEntity> getIdleActivityList(long taskId);

	List<ExternalActivityEntity> getExternalActivityList(long taskId);

	List<EditorActivityEntity> getEditorActivityList(long taskId);

	List<EventEntity> getEventList(long taskId);

	LocalDateTime getMostRecentActivityEnd(long taskId);

	void saveActiveState(IdeaFlowPartialStateEntity activeState);

	void saveActiveState(IdeaFlowPartialStateEntity activeState, IdeaFlowPartialStateEntity containingState);

	void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowPartialStateEntity activeState);


	<T extends ActivityEntity> T saveActivity(T activity);

	EventEntity saveEvent(EventEntity event);

	TaskEntity saveTask(TaskEntity task);

	TaskEntity findTaskWithId(long taskId);

	TaskEntity findTaskWithName(String taskName);

	List<TaskEntity> findRecentTasks(int limit);

}

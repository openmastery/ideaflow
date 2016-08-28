package org.openmastery.publisher.core;

import org.openmastery.publisher.core.activity.ActivityEntity;
import org.openmastery.publisher.core.activity.ActivityRepository;
import org.openmastery.publisher.core.activity.EditorActivityEntity;
import org.openmastery.publisher.core.activity.ExternalActivityEntity;
import org.openmastery.publisher.core.activity.IdleActivityEntity;
import org.openmastery.publisher.core.event.EventEntity;
import org.openmastery.publisher.core.event.EventRepository;
import org.openmastery.publisher.core.ideaflow.IdeaFlowPartialStateEntity;
import org.openmastery.publisher.core.ideaflow.IdeaFlowPartialStateRepository;
import org.openmastery.publisher.core.ideaflow.IdeaFlowPartialStateScope;
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity;
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateRepository;
import org.openmastery.publisher.core.task.TaskEntity;
import org.openmastery.publisher.core.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
public class IdeaFlowRelationalPersistenceService implements IdeaFlowPersistenceService {

	@Autowired
	private IdeaFlowStateRepository ideaFlowStateRepository;
	@Autowired
	private IdeaFlowPartialStateRepository ideaFlowPartialStateRepository;
	@Autowired
	private ActivityRepository activityRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private TaskRepository taskRepository;

	private IdeaFlowPartialStateEntity getPartialState(long taskId, IdeaFlowPartialStateScope scope) {
		IdeaFlowPartialStateEntity.PrimaryKey pk = IdeaFlowPartialStateEntity.PrimaryKey.builder()
				.taskId(taskId)
				.scope(scope)
				.build();
		return ideaFlowPartialStateRepository.findOne(pk);
	}

	@Override
	public IdeaFlowPartialStateEntity getActiveState(long taskId) {
		return getPartialState(taskId, IdeaFlowPartialStateScope.ACTIVE);
	}

	@Override
	public IdeaFlowPartialStateEntity getContainingState(long taskId) {
		return getPartialState(taskId, IdeaFlowPartialStateScope.CONTAINING);
	}

	@Override
	public List<IdeaFlowStateEntity> getStateList(long taskId) {
		return ideaFlowStateRepository.findByTaskId(taskId);
	}

	@Override
	public List<ActivityEntity> getActivityList(long taskId) {
		return activityRepository.findByTaskId(taskId);
	}

	@Override
	public List<IdleActivityEntity> getIdleActivityList(long taskId) {
		return activityRepository.findIdleActivityByTaskId(taskId);
	}

	@Override
	public List<ExternalActivityEntity> getExternalActivityList(long taskId) {
		return activityRepository.findExternalActivityByTaskId(taskId);
	}

	@Override
	public List<EventEntity> getEventList(long taskId) {
		return eventRepository.findByTaskId(taskId);
	}

	@Override
	public List<EditorActivityEntity> getEditorActivityList(long taskId) {
		return activityRepository.findEditorActivityByTaskId(taskId);
	}

	@Override
	public LocalDateTime getMostRecentActivityEnd(long taskId) {
		ActivityEntity activity = activityRepository.findMostRecentActivityForTask(taskId);
		return activity != null ? activity.getEnd() : null;
	}

	@Override
	public void saveActiveState(IdeaFlowPartialStateEntity activeState) {
		saveActiveState(activeState, null);
	}

	@Override
	public void saveActiveState(IdeaFlowPartialStateEntity activeState, IdeaFlowPartialStateEntity containingState) {
		activeState.setScope(IdeaFlowPartialStateScope.ACTIVE);
		ideaFlowPartialStateRepository.save(activeState);
		if (containingState != null) {
			containingState.setScope(IdeaFlowPartialStateScope.CONTAINING);
			ideaFlowPartialStateRepository.save(containingState);
		} else {
			ideaFlowPartialStateRepository.deleteIfExists(activeState.getTaskId(), IdeaFlowPartialStateScope.CONTAINING.toString());
		}
	}

	@Override
	public void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowPartialStateEntity activeState) {
		ideaFlowStateRepository.save(stateToSave);
		saveActiveState(activeState);
	}

	@Override
	public <T extends ActivityEntity> T saveActivity(T activity) {
		return activityRepository.save(activity);
	}

	@Override
	public EventEntity saveEvent(EventEntity event) {
		return eventRepository.save(event);
	}

	@Override
	public TaskEntity saveTask(TaskEntity task) {
		return taskRepository.save(task);
	}

	@Override
	public TaskEntity findTaskWithId(long taskId) {
		return taskRepository.findOne(taskId);
	}

	@Override
	public TaskEntity findTaskWithName(String taskName) {
		return taskRepository.findByName(taskName);
	}

	@Override
	public List<TaskEntity> findRecentTasks(int limit) {
		return taskRepository.findRecent(limit);
	}

}

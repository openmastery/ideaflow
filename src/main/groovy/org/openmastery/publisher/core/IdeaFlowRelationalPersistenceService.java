package org.openmastery.publisher.core;

import org.openmastery.publisher.core.activity.EditorActivityEntity;
import org.openmastery.publisher.core.activity.EditorActivityRepository;
import org.openmastery.publisher.core.activity.IdleTimeBandEntity;
import org.openmastery.publisher.core.activity.IdleTimeBandRepository;
import org.openmastery.publisher.core.event.EventEntity;
import org.openmastery.publisher.core.event.EventRepository;
import org.openmastery.publisher.core.ideaflow.IdeaFlowPartialStateEntity;
import org.openmastery.publisher.core.ideaflow.IdeaFlowPartialStateRepository;
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity;
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateRepository;
import org.openmastery.publisher.core.task.TaskEntity;
import org.openmastery.publisher.core.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdeaFlowRelationalPersistenceService implements IdeaFlowPersistenceService {

	private Map<Long, IdeaFlowPartialStateEntity> activeStateMap = new HashMap<>();
	private Map<Long, IdeaFlowPartialStateEntity> containingStateMap = new HashMap<>();

	@Autowired
	private IdeaFlowStateRepository ideaFlowStateRepository;
	@Autowired
	private IdeaFlowPartialStateRepository ideaFlowPartialStateRepository;
	@Autowired
	private IdleTimeBandRepository idleTimeBandRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private EditorActivityRepository editorActivityRepository;
	@Autowired
	private TaskRepository taskRepository;

	@Override
	public IdeaFlowPartialStateEntity getActiveState(long taskId) {
//		IdeaFlowPartialStateEntity.PrimaryKey pk = IdeaFlowPartialStateEntity.PrimaryKey.builder()
//				.taskId(taskId)
//				.scope(IdeaFlowPartialStateScope.ACTIVE)
//				.build();
//
//		ideaFlowPartialStateRepository.findOne()
		return activeStateMap.get(taskId);
	}

	@Override
	public IdeaFlowPartialStateEntity getContainingState(long taskId) {
		return containingStateMap.get(taskId);
	}

	@Override
	public List<IdeaFlowStateEntity> getStateList(long taskId) {
		return ideaFlowStateRepository.findByTaskId(taskId);
	}

	@Override
	public List<IdleTimeBandEntity> getIdleTimeBandList(long taskId) {
		return idleTimeBandRepository.findByTaskId(taskId);
	}

	@Override
	public List<EventEntity> getEventList(long taskId) {
		return eventRepository.findByTaskId(taskId);
	}

	@Override
	public List<EditorActivityEntity> getEditorActivityList(long taskId) {
		return editorActivityRepository.findByTaskId(taskId);
	}

	@Override
	public LocalDateTime getMostRecentActivityEnd(long taskId) {
		EditorActivityEntity editorActivity = editorActivityRepository.findMostRecentEditorActivityForTask(taskId);
		return editorActivity != null ? editorActivity.getEnd() : null;
	}

	@Override
	public void saveActiveState(IdeaFlowPartialStateEntity activeState) {
		saveActiveState(activeState, null);
	}

	@Override
	public void saveActiveState(IdeaFlowPartialStateEntity activeState, IdeaFlowPartialStateEntity containingState) {
		activeStateMap.put(activeState.getTaskId(), activeState);
		if (containingState != null) {
			containingStateMap.put(containingState.getTaskId(), containingState);
		} else {
			containingStateMap.remove(activeState.getTaskId());
		}
	}

	@Override
	@Transactional
	public void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowPartialStateEntity activeState) {
		ideaFlowStateRepository.save(stateToSave);
		saveActiveState(activeState);
	}

	@Override
	public IdleTimeBandEntity saveIdleActivity(IdleTimeBandEntity idleActivity) {
		return idleTimeBandRepository.save(idleActivity);
	}

	@Override
	public EventEntity saveEvent(EventEntity event) {
		return eventRepository.save(event);
	}

	@Override
	public EditorActivityEntity saveEditorActivity(EditorActivityEntity activity) {
		return editorActivityRepository.save(activity);
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

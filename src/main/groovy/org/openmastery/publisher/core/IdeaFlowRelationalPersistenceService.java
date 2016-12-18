/**
 * Copyright 2016 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.core;

import org.openmastery.publisher.core.activity.*;
import org.openmastery.publisher.core.event.EventEntity;
import org.openmastery.publisher.core.event.EventRepository;
import org.openmastery.publisher.ideaflow.IdeaFlowPartialStateEntity;
import org.openmastery.publisher.ideaflow.IdeaFlowPartialStateRepository;
import org.openmastery.publisher.ideaflow.IdeaFlowPartialStateScope;
import org.openmastery.publisher.ideaflow.IdeaFlowStateEntity;
import org.openmastery.publisher.ideaflow.IdeaFlowStateRepository;
import org.openmastery.publisher.core.task.TaskEntity;
import org.openmastery.publisher.core.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.sql.Timestamp;
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
	public List<ModificationActivityEntity> getModificationActivityList(long taskId) {
		return activityRepository.findModificationActivityByTaskId(taskId);
	}

	@Override
	public List<ExecutionActivityEntity> getExecutionActivityList(long taskId) {
		return activityRepository.findExecutionActivityByTaskId(taskId);
	}

	@Override
	public List<BlockActivityEntity> getBlockActivityList(long taskId) {
		return activityRepository.findBlockActivityByTaskId(taskId);
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
	public TaskEntity findTaskWithName(Long userId, String taskName) {
		return taskRepository.findByOwnerIdAndName(userId, taskName);
	}

	@Override
	public List<EventEntity> findRecentEvents(Long userId, Timestamp afterDate, Integer limit) {
		return eventRepository.findRecentEvents(userId, afterDate, limit);
	}

	@Override
	public List<TaskEntity> findRecentTasks(Long userId, int limit) {
		return taskRepository.findRecent(userId, limit);
	}

}

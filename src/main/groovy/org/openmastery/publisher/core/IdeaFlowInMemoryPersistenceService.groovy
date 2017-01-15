/*
 * Copyright 2017 New Iron Group, Inc.
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
package org.openmastery.publisher.core

import org.openmastery.publisher.core.activity.ActivityEntity
import org.openmastery.publisher.core.activity.BlockActivityEntity
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.openmastery.publisher.core.annotation.AnnotationEntity
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.annotation.SnippetAnnotationEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.ideaflow.IdeaFlowPartialStateEntity
import org.openmastery.publisher.ideaflow.IdeaFlowStateEntity
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.time.TimeConverter
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

import java.sql.Timestamp
import java.time.LocalDateTime

public class IdeaFlowInMemoryPersistenceService implements IdeaFlowPersistenceService {

	private long ideaFlowStateId = 1L
	private long eventId = 1L
	private long taskId = 1L
	private long activityId = 1L
	private long annotationId = 1L
	private IdeaFlowPartialStateEntity activeState
	private IdeaFlowPartialStateEntity containingState
	private List<IdeaFlowStateEntity> stateList = []
	private List<ActivityEntity> activityList = []
	private List<EventEntity> eventList = []
	private List<TaskEntity> taskList = []
	private List<AnnotationEntity> annotationList = []

	@Override
	public IdeaFlowPartialStateEntity getActiveState(long taskId) {
		activeState
	}

	@Override
	public IdeaFlowPartialStateEntity getContainingState(long taskId) {
		containingState
	}

	@Override
	public List<IdeaFlowStateEntity> getStateList(long taskId) {
		stateList.findAll { it.taskId == taskId }
	}

	@Override
	public List<ActivityEntity> getActivityList(long taskId) {
		activityList.findAll { it.taskId == taskId }
	}

	@Override
	public List<IdleActivityEntity> getIdleActivityList(long taskId) {
		findAllActivitiesOfType(IdleActivityEntity, taskId)
	}

	@Override
	public List<ExternalActivityEntity> getExternalActivityList(long taskId) {
		findAllActivitiesOfType(ExternalActivityEntity, taskId)
	}

	@Override
	List<EditorActivityEntity> getEditorActivityList(long taskId) {
		findAllActivitiesOfType(EditorActivityEntity, taskId)
	}

	@Override
	List<ModificationActivityEntity> getModificationActivityList(long taskId) {
		findAllActivitiesOfType(ModificationActivityEntity, taskId)
	}

	@Override
	List<ExecutionActivityEntity> getExecutionActivityList(long taskId) {
		findAllActivitiesOfType(ExecutionActivityEntity, taskId)
	}

	@Override
	List<BlockActivityEntity> getBlockActivityList(long taskId) {
		findAllActivitiesOfType(BlockActivityEntity, taskId)
	}

	@Override
	public List<FaqAnnotationEntity> getFaqAnnotationList(long taskId) {
		return findAllAnnotationsByType(FaqAnnotationEntity, taskId);
	}

	@Override
	public List<SnippetAnnotationEntity> getSnippetAnnotationList(long taskId) {
		return findAllAnnotationsByType(SnippetAnnotationEntity, taskId);
	}

	@Override
	EventEntity findEventById(long eventId) {
		return eventList.find { it.id == eventId }
	}

	@Override
	void deleteFAQAnnotation(long eventId) {
		annotationList.removeAll { it.eventId == eventId && it instanceof FaqAnnotationEntity}
	}

	private <T> List<T> findAllActivitiesOfType(Class<T> type, long taskId) {
		activityList.findAll { type.isInstance(it) && it.taskId == taskId }
	}

	private <T> List<T> findAllAnnotationsByType(Class<T> type, long taskId) {
		annotationList.findAll { type.isInstance(it) && it.taskId == taskId }
	}


	@Override
	LocalDateTime getMostRecentActivityEnd(long taskId) {
		LocalDateTime mostRecentActivity = null
		activityList.each { ActivityEntity activity ->
			if ((mostRecentActivity == null) || (mostRecentActivity.isBefore(activity.end))) {
				mostRecentActivity = activity.end
			}
		}
		mostRecentActivity
	}

	@Override
	public List<EventEntity> getEventList(long taskId) {
		eventList.findAll { it.taskId == taskId }
	}

	@Override
	public void saveActiveState(IdeaFlowPartialStateEntity activeState) {
		saveActiveState(activeState, null)
	}

	@Override
	public void saveActiveState(IdeaFlowPartialStateEntity activeState, IdeaFlowPartialStateEntity containingState) {
		this.activeState = activeState
		this.containingState = containingState
	}

	@Override
	public void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowPartialStateEntity activeState) {
		stateToSave.id = ideaFlowStateId++
		stateList.add(stateToSave)
		saveActiveState(activeState)
	}

	@Override
	public <T extends ActivityEntity> T saveActivity(T activity) {
		activity.id = activityId++
		activityList.add(activity)
		activity
	}

	@Override
	public <T extends AnnotationEntity> T saveAnnotation(T annotation) {
		annotation.id = annotationId++
		annotationList.add(annotation)
		annotation
	}

	@Override
	public EventEntity saveEvent(EventEntity event) {
		event.id = eventId++
		eventList.add(event)
		event
	}

	@Override
	TaskEntity saveTask(TaskEntity task) {
		if (taskList.find { it.name == task.name }) {
			throw new DataIntegrityViolationException("Duplicate task");
		}

		task.id = taskId++
		taskList.add(task)
		task
	}

	@Override
	void deleteTask(TaskEntity task) {
		taskList.remove(task)
	}

	@Override
	TaskEntity findTaskWithId(long taskId) {
		taskList.find {
			it.id == taskId
		}
	}

	@Override
	TaskEntity findTaskWithName(Long userId, String taskName) {
		taskList.find {
			it.name == taskName
		}
	}

	@Override
	List<EventEntity> findRecentEvents(Long userId, Timestamp afterDate, Integer limit) {
		List<EventEntity> eventList = eventList.findAll() { EventEntity event ->
			event.position.equals(TimeConverter.toJavaLocalDateTime(afterDate)) ||
					event.position.isAfter(TimeConverter.toJavaLocalDateTime(afterDate))
		}.sort {
			eventList.position
		}
		if (eventList.size() > limit) {
			eventList = eventList.subList(0, limit)
		}
		return eventList
	}


}

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

import org.openmastery.publisher.core.activity.ActivityEntity;
import org.openmastery.publisher.core.activity.EditorActivityEntity;
import org.openmastery.publisher.core.activity.ExternalActivityEntity;
import org.openmastery.publisher.core.activity.IdleActivityEntity;
import org.openmastery.publisher.core.event.EventEntity;
import org.openmastery.publisher.core.ideaflow.IdeaFlowPartialStateEntity;
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity;
import org.openmastery.publisher.core.task.TaskEntity;
import org.openmastery.publisher.core.user.UserEntity;

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

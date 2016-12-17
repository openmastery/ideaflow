/*
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
package org.openmastery.publisher.core.task

import com.bancvue.rest.exception.ConflictingEntityException
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.task.NewTask
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

@Component
class TaskService {

	@Autowired
	private IdeaFlowPersistenceService persistenceService;
	@Autowired
	private TimeService timeService;

	private EntityMapper entityMapper = new EntityMapper();


	public Task create(Long userId, NewTask newTask) {
		TaskEntity task = TaskEntity.builder()
				.ownerId(userId)
				.name(newTask.getName())
				.description(newTask.getDescription())
				.project(newTask.getProject())
				.creationDate(timeService.javaNow())
				.modifyDate(timeService.javaNow())
				.build();

		TaskEntity existingTask = persistenceService.findTaskWithName(task.getName());
		if (existingTask != null) {
			throw new ConflictingTaskException(toApiTask(existingTask));
		}

		try {
			task = persistenceService.saveTask(task);
		} catch (DataIntegrityViolationException ex) {
			existingTask = persistenceService.findTaskWithName(task.getName());
			throw new ConflictingTaskException(toApiTask(existingTask));
		}

		Task apiTask = entityMapper.mapIfNotNull(task, Task.class);
		return apiTask;
	}

	public Task findTaskWithId(Long taskId) {
		TaskEntity taskEntity = persistenceService.findTaskWithId(taskId);
		return toApiTask(taskEntity);
	}

	public Task findTaskWithName(String taskName) {
		TaskEntity taskEntity = persistenceService.findTaskWithName(taskName);
		return toApiTask(taskEntity);
	}

	//TODO implement paging
	public List<Task> findRecentTasks(Long userId, Integer page, Integer perPage) {
		List<TaskEntity> taskList = persistenceService.findRecentTasks(userId, perPage);
		return entityMapper.mapList(taskList, Task.class);
	}

	private Task toApiTask(TaskEntity taskEntity) {
		return entityMapper.mapIfNotNull(taskEntity, Task.class);
	}


	static class ConflictingTaskException extends ConflictingEntityException {
		ConflictingTaskException(Task existingTask) {
			super("Task with name '" + existingTask.getName() + "' already exists", existingTask);
		}
	}

}

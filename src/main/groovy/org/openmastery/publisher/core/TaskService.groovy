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

import com.bancvue.rest.exception.ConflictException
import com.bancvue.rest.exception.NotFoundException
import org.openmastery.mapper.ValueObjectMapper
import org.openmastery.publisher.api.PagedResult
import org.openmastery.publisher.api.task.NewTask
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.api.task.TaskPatch
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.publisher.core.task.TaskRepository
import org.openmastery.publisher.security.InvocationContext
import org.openmastery.storyweb.core.SearchUtils
import org.openmastery.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class TaskService {

	//@Autowired
	//private IdeaFlowPersistenceService persistenceService;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private TimeService timeService;
	@Autowired
	private InvocationContext invocationContext;

	private ValueObjectMapper entityMapper = new ValueObjectMapper();

	public Task create(NewTask newTask) {
		long userId = invocationContext.getUserId()

		TaskEntity task = TaskEntity.builder()
				.ownerId(userId)
				.name(newTask.getName())
				.description(newTask.getDescription())
				.project(newTask.getProject())
				.creationDate(timeService.now())
				.modifyDate(timeService.now())
				.build();

		TaskEntity existingTask = taskRepository.findByOwnerIdAndName(userId, task.getName());
		if (existingTask != null) {
			throw new ConflictException(toApiTask(existingTask));
		}

		try {
			task = taskRepository.save(task);
		} catch (DataIntegrityViolationException ex) {
			existingTask = taskRepository.findByOwnerIdAndName(userId, task.getName());
			throw new ConflictingTaskException(toApiTask(existingTask));
		}

		Task apiTask = entityMapper.mapIfNotNull(task, Task.class);
		return apiTask;
	}

	public Task findTaskWithId(Long taskId) {
		TaskEntity taskEntity = taskRepository.findOne(taskId);
		if (taskEntity == null) {
			throw new NotFoundException("No task found with id=${taskId}");
		}
		return toApiTask(taskEntity);
	}

	public Task findTaskWithName(String taskName) {
		TaskEntity taskEntity = taskRepository.findByOwnerIdAndName(invocationContext.getUserId(), taskName);
		return toApiTask(taskEntity);
	}


	public PagedResult<Task> findRecentTasksMatchingTags(String optionalProject, List<String> tags, int pageNumber, int elementsPerPage) {
		String tagPattern = SearchUtils.createSearchPattern(tags)
		String projectLikeClause = generateProjectLikeClause(optionalProject)
		Long userId = invocationContext.getUserId()

		int recordCount = taskRepository.countTasksMatchingTags(userId, projectLikeClause, tagPattern)

		if (recordCount > 0) {
			PagedResult<Task> pagedResult = createPagedResult(recordCount, pageNumber, elementsPerPage)
			List<TaskEntity> taskEntity = taskRepository.findByOwnerIdAndMatchingTags(userId, projectLikeClause, tagPattern, elementsPerPage, pageNumber * elementsPerPage);
			pagedResult.contents = entityMapper.mapList(taskEntity, Task.class)
			return pagedResult
		} else {
			return createPagedResult(0, pageNumber, elementsPerPage)
		}
	}

    public PagedResult<Task> findRecentTasks(String optionalProject, int pageNumber, int elementsPerPage) {
		String projectLikeClause = generateProjectLikeClause(optionalProject)

		PageRequest pageRequest = new PageRequest(pageNumber, elementsPerPage, Sort.Direction.DESC, "modifyDate")
		Page<TaskEntity> taskEntityPage = taskRepository.findByOwnerIdAndProjectLike(invocationContext.getUserId(), projectLikeClause, pageRequest);
        return toPagedResult(taskEntityPage)
    }

	private String generateProjectLikeClause(String optionalProject) {
		String projectLikeClause = "%"
		if (optionalProject != null) {
			projectLikeClause = optionalProject
		}
		return projectLikeClause
	}

	private PagedResult<Task> createPagedResult(int recordCount, int pageNumber, int elementsPerPage) {
		PagedResult pagedResult = PagedResult.create(recordCount, pageNumber, elementsPerPage)
		pagedResult.addSortOrder("modifyDate", PagedResult.SortOrder.Direction.DESC)
		return pagedResult
	}

	private PagedResult<Task> toPagedResult(Page<?> dataPage) {
		PagedResult pagedResult = new PagedResult()
		pagedResult.hasNext = dataPage.hasNext()
		pagedResult.hasPrevious = dataPage.hasPrevious()
		pagedResult.pageNumber = dataPage.number
		pagedResult.totalPages = dataPage.totalPages
		pagedResult.totalElements = dataPage.totalElements
		pagedResult.elementsPerPage = dataPage.size
		pagedResult.contents = entityMapper.mapList(dataPage.content, Task.class)
		pagedResult.addSortOrder("modifyDate", PagedResult.SortOrder.Direction.DESC)
		return pagedResult
	}

	private Task toApiTask(TaskEntity taskEntity) {
		return entityMapper.mapIfNotNull(taskEntity, Task.class);
	}

	Task updateTask(Task taskWithUpdates) {
		TaskEntity taskEntity = taskRepository.findOne(taskWithUpdates.id)
		taskEntity.description = taskWithUpdates.description
		taskEntity.project = taskWithUpdates.project
		taskEntity.modifyDate = timeService.now()
		TaskEntity savedEntity = taskRepository.save(taskEntity)
		return toApiTask(savedEntity);
	}

	Task updateTask(Long taskId, TaskPatch taskPatch) {
		TaskEntity taskEntity = taskRepository.findOne(taskId)

		if (taskPatch.name) {
			taskEntity.name = taskPatch.name
		}
		if (taskPatch.description) {
			taskEntity.description = taskPatch.description
		}
		if (taskPatch.project) {
			taskEntity.project = taskPatch.project
		}

		taskEntity.modifyDate = timeService.now()
		TaskEntity savedEntity = taskRepository.save(taskEntity)
		return toApiTask(savedEntity);
	}


	static class ConflictingTaskException extends ConflictException {
		ConflictingTaskException(Task existingTask) {
			super("Task with name '" + existingTask.getName() + "' already exists", existingTask);
		}
	}


}

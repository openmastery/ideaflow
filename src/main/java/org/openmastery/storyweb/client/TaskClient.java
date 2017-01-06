package org.openmastery.storyweb.client;

import org.openmastery.publisher.api.task.NewTask;
import org.openmastery.publisher.api.task.Task;
import org.openmastery.storyweb.api.ResourcePaths;

import java.util.List;

public class TaskClient extends StorywebClient<Task, TaskClient> {

	public TaskClient(String baseUrl) {
		super(baseUrl, ResourcePaths.STORY_WEB_PATH + ResourcePaths.TASK_PATH, Task.class);
	}

	public Task createTask(String taskName, String description, String project) {
		NewTask task = NewTask.builder()
				.name(taskName)
				.description(description)
				.project(project)
				.build();
		return crudClientRequest.createWithPost(task);
	}

	public Task findTaskWithName(String taskName) {
		return crudClientRequest.path(ResourcePaths.TASK_NAME_PATH)
				.path(taskName).find();
	}

	public List<Task> findRecentTasks(Integer page, Integer perPage) {
		return crudClientRequest
				.queryParam("page", page)
				.queryParam("per_page", perPage).findMany();
	}


}

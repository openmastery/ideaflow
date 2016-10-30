package org.openmastery.publisher.client;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.task.NewTask;
import org.openmastery.publisher.api.task.Task;

import java.util.List;

public class TaskClient extends OpenMasteryClient<Task, TaskClient> {

	public TaskClient(String baseUrl) {
		super(baseUrl, ResourcePaths.TASK_PATH, Task.class);
	}

	public Task createTask(String taskName, String description) {
		NewTask task = NewTask.builder()
				.name(taskName)
				.description(description)
				.build();
		return crudClientRequest.createWithPost(task);
	}

	public Task findTaskWithName(String taskName) {
		return crudClientRequest.queryParam("taskName", taskName).find();
	}

	public List<Task> findRecentTasks(int limit) {
		return crudClientRequest
				.path(ResourcePaths.RECENT_PATH)
				.queryParam("limit", limit)
				.findMany();
	}

	public Task activate(Long taskId) {
		return crudClientRequest
				.path(ResourcePaths.ACTIVATE_PATH)
				.path(taskId)
				.updateWithPut("");
	}

}

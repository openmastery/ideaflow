package org.ideaflow.publisher.client;

import org.openmastery.rest.client.CrudClient;
import org.ideaflow.publisher.api.task.NewTask;
import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.task.Task;

public class TaskClient extends CrudClient<Task, TaskClient> {

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

}

package org.ideaflow.publisher.client;

import org.ideaflow.common.rest.client.CrudClient;
import org.ideaflow.publisher.api.task.NewTask;
import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.task.Task;

public class TaskClient extends CrudClient<Task, TaskClient> {

	public TaskClient(String baseUrl) {
		super(baseUrl, ResourcePaths.TASK_PATH, Task.class);
	}

	@SuppressWarnings("unchecked")
	public Task createTask(String taskName, String projectName) {
		NewTask task = NewTask.builder()
				.taskName(taskName)
				.projectName(projectName)
				.build();
		return crudClientRequest.createWithPost(task);
	}

	public Task getActiveTask() {
		return crudClientRequest.find();
	}

}

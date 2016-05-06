package org.ideaflow.publisher.client;

import org.ideaflow.common.rest.client.CrudClient;
import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.Task;

public class TaskClient extends CrudClient<Task, TaskClient> {

	public TaskClient(String baseUrl) {
		super(baseUrl, ResourcePaths.TASK_PATH, Task.class);
	}

	public void activateTask(String taskId) {
		crudClientRequest.path(taskId)
				.updateWithPut(null);
	}

	public void createTask(Task task) {
		crudClientRequest.createWithPost(task);
	}

	public Task getActiveTask() {
		return crudClientRequest.find();
	}

}

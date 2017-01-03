package org.openmastery.publisher.client;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.task.NewTask;
import org.openmastery.publisher.api.task.Task;
import org.openmastery.publisher.api.ResourcePage;

public class TaskClient extends IdeaFlowClient<Object, TaskClient> {

	public TaskClient(String baseUrl) {
		super(baseUrl, ResourcePaths.IDEAFLOW_PATH + ResourcePaths.TASK_PATH, Object.class);
	}

	public Task createTask(String taskName, String description, String project) {
		NewTask task = NewTask.builder()
				.name(taskName)
				.description(description)
				.project(project)
				.build();
		return (Task)crudClientRequest.entity(Task.class).createWithPost(task);
	}

	public Task findTaskWithName(String taskName) {
		return (Task)crudClientRequest.path(ResourcePaths.TASK_NAME_PATH)
				.path(taskName).entity(Task.class).find();
	}

	public ResourcePage<Task> findRecentTasks(Integer page, Integer perPage) {
        return (ResourcePage<Task>) getUntypedCrudClientRequest()
                .queryParam("page", page)
                .queryParam("per_page", perPage)
                .entity(ResourcePage.class).find();
	}

}

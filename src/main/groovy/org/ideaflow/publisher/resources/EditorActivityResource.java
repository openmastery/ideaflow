package org.ideaflow.publisher.resources;

import org.ideaflow.publisher.api.EditorActivity;
import org.ideaflow.publisher.api.IdleActivity;
import org.ideaflow.publisher.api.ResourcePaths;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.TASK_PATH + "/{taskId}" + ResourcePaths.ACTIVITY_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class EditorActivityResource {

	@POST
	@Path(ResourcePaths.EDITOR_PATH)
	public void addEditorActivity(@PathParam("taskId") String taskId, EditorActivity editorActivity) {

	}

	@POST
	@Path(ResourcePaths.IDLE_PATH)
	public void addIdleActivity(@PathParam("taskId") String taskId, IdleActivity idleActivity) {

	}

}

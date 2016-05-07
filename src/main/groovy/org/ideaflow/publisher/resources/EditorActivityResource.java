package org.ideaflow.publisher.resources;

import org.ideaflow.publisher.api.activity.EditorActivity;
import org.ideaflow.publisher.api.activity.IdleActivity;
import org.ideaflow.publisher.api.ResourcePaths;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.ACTIVITY_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class EditorActivityResource {

	@POST
	@Path(ResourcePaths.EDITOR_PATH)
	public void addEditorActivity(EditorActivity editorActivity) {

	}

	@POST
	@Path(ResourcePaths.IDLE_PATH)
	public void addIdleActivity(IdleActivity idleActivity) {

	}

}

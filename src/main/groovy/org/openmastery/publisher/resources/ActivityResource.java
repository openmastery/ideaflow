package org.openmastery.publisher.resources;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.activity.EditorActivity;
import org.openmastery.publisher.core.activity.EditorActivityEntity;
import org.openmastery.mapper.EntityMapper;
import org.openmastery.time.TimeService;
import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;

@Component
@Path(ResourcePaths.ACTIVITY_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class ActivityResource {

	@Autowired
	private IdeaFlowPersistenceService persistenceService;
	@Autowired
	private TimeService timeService;
	private EntityMapper entityMapper = new EntityMapper();

	@POST
	@Path(ResourcePaths.EDITOR_PATH)
	public void addEditorActivity(EditorActivity editorActivity) {
		LocalDateTime end = timeService.now();
		LocalDateTime start = end.minus(editorActivity.getDuration());
		EditorActivityEntity entity = entityMapper.mapIfNotNull(editorActivity, EditorActivityEntity.class);
		entity.setStart(start);
		entity.setEnd(end);
		persistenceService.saveEditorActivity(entity);
	}

}

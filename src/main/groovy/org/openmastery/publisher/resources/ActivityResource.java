/**
 * Copyright 2016 New Iron Group, Inc.
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
package org.openmastery.publisher.resources;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.activity.NewActivityBatch;
import org.openmastery.publisher.api.activity.NewEditorActivity;
import org.openmastery.publisher.api.activity.NewExternalActivity;
import org.openmastery.publisher.api.activity.NewIdleActivity;
import org.openmastery.publisher.core.activity.EditorActivityEntity;
import org.openmastery.mapper.EntityMapper;
import org.openmastery.publisher.core.activity.ExternalActivityEntity;
import org.openmastery.publisher.core.activity.IdleActivityEntity;
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
	public void addEditorActivity(NewEditorActivity editorActivity) {
		LocalDateTime end = timeService.now();
		LocalDateTime start = end.minusSeconds(editorActivity.getDurationInSeconds());
		EditorActivityEntity entity = entityMapper.mapIfNotNull(editorActivity, EditorActivityEntity.class);
		entity.setStart(start);
		entity.setEnd(end);
		persistenceService.saveActivity(entity);
	}

	@POST
	@Path(ResourcePaths.IDLE_PATH)
	public void addIdleActivity(NewIdleActivity idleActivity) {
		LocalDateTime end = timeService.now();
		LocalDateTime start = end.minusSeconds(idleActivity.getDurationInSeconds());
		IdleActivityEntity entity = entityMapper.mapIfNotNull(idleActivity, IdleActivityEntity.class);
		entity.setStart(start);
		entity.setEnd(end);
		persistenceService.saveActivity(entity);
	}

	@POST
	@Path(ResourcePaths.EXTERNAL_PATH)
	public void addExternalActivity(NewExternalActivity externalActivity) {
		LocalDateTime end = timeService.now();
		LocalDateTime start = end.minusSeconds(externalActivity.getDurationInSeconds());
		ExternalActivityEntity entity = entityMapper.mapIfNotNull(externalActivity, ExternalActivityEntity.class);
		entity.setStart(start);
		entity.setEnd(end);
		persistenceService.saveActivity(entity);
	}

	@POST
	public void addActivityBatch(NewActivityBatch batch) {
		// TODO: fill me in
	}

}

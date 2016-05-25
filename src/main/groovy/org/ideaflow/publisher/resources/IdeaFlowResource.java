/**
 * Copyright 2015 New Iron Group, Inc.
 * <p>
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/gpl-3.0.en.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ideaflow.publisher.resources;

import org.openmastery.mapper.EntityMapper;
import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.ideaflow.IdeaFlowState;
import org.ideaflow.publisher.api.ideaflow.IdeaFlowStateTransition;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateEntity;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateMachine;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateMachineFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.IDEAFLOW_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class IdeaFlowResource {

	@Autowired
	private IdeaFlowStateMachineFactory stateMachineFactory;
	@Autowired
	private IdeaFlowPersistenceService persistenceService;
	private EntityMapper entityMapper = new EntityMapper();

	private IdeaFlowStateMachine createStateMachine(IdeaFlowStateTransition transition) {
		return stateMachineFactory.createStateMachine(transition.getTaskId());
	}

	@POST
	@Path(ResourcePaths.CONFLICT_PATH + ResourcePaths.START_PATH)
	public void startConflict(IdeaFlowStateTransition transition) {
		createStateMachine(transition).startConflict(transition.getComment());
	}

	@POST
	@Path(ResourcePaths.CONFLICT_PATH + ResourcePaths.STOP_PATH)
	public void endConflict(IdeaFlowStateTransition transition) {
		createStateMachine(transition).endConflict(transition.getComment());
	}

	@POST
	@Path(ResourcePaths.LEARNING_PATH + ResourcePaths.START_PATH)
	public void startLearning(IdeaFlowStateTransition transition) {
		createStateMachine(transition).startLearning(transition.getComment());
	}

	@POST
	@Path(ResourcePaths.LEARNING_PATH + ResourcePaths.STOP_PATH)
	public void endLearning(IdeaFlowStateTransition transition) {
		createStateMachine(transition).endLearning(transition.getComment());
	}

	@POST
	@Path(ResourcePaths.REWORK_PATH + ResourcePaths.START_PATH)
	public void startRework(IdeaFlowStateTransition transition) {
		createStateMachine(transition).startRework(transition.getComment());
	}

	@POST
	@Path(ResourcePaths.REWORK_PATH + ResourcePaths.STOP_PATH)
	public void endRework(IdeaFlowStateTransition transition) {
		createStateMachine(transition).endRework(transition.getComment());
	}

	@GET
	@Path(ResourcePaths.ACTIVE_STATE_PATH + "/{taskId}")
	public IdeaFlowState activeState(@PathParam("taskId") Long taskId) {
		IdeaFlowStateEntity entity = persistenceService.getActiveState(taskId);
		return entityMapper.mapIfNotNull(entity, IdeaFlowState.class);
	}

}

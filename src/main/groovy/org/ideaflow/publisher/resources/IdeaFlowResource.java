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

import org.ideaflow.publisher.api.IdeaFlowState;
import org.ideaflow.publisher.api.ResourcePaths;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;

@Component
@Path(ResourcePaths.TASK_PATH + "/{taskId}" + ResourcePaths.IDEAFLOW_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class IdeaFlowResource {

	@POST
	@Path(ResourcePaths.CONFLICT_PATH + ResourcePaths.START_PATH)
	public void startConflict(@PathParam("taskId") String taskId, String question) {
		System.out.println("Start Conflict: " + taskId + ", " + question);
	}

	@POST
	@Path(ResourcePaths.CONFLICT_PATH + ResourcePaths.STOP_PATH)
	public void endConflict(@PathParam("taskId") String taskId, String resolution) {
		System.out.println("Stop Conflict: " + taskId + ", " + resolution);
	}

	@POST
	@Path(ResourcePaths.LEARNING_PATH + ResourcePaths.START_PATH)
	public void startLearning(@PathParam("taskId") String taskId, String comment) {
		System.out.println("Start Learning: " + taskId + ", " + comment);
	}

	@POST
	@Path(ResourcePaths.LEARNING_PATH + ResourcePaths.STOP_PATH)
	public void endLearning(@PathParam("taskId") String taskId) {
		System.out.println("Stop Learning: " + taskId);
	}

	@POST
	@Path(ResourcePaths.REWORK_PATH + ResourcePaths.START_PATH)
	public void startRework(@PathParam("taskId") String taskId, String comment) {
		System.out.println("Start Rework: " + taskId + ", " + comment);
	}

	@POST
	@Path(ResourcePaths.REWORK_PATH + ResourcePaths.STOP_PATH)
	public void endRework(@PathParam("taskId") String taskId) {
		System.out.println("Stop Rework: " + taskId);
	}

	@GET
	@Path(ResourcePaths.ACTIVE_STATE_PATH)
	public IdeaFlowState activeState(@PathParam("taskId") String taskId) {
		System.out.println("Get Active State: " + taskId);
		return IdeaFlowState.builder()
				.start(LocalDateTime.now())
				.end(LocalDateTime.now())
				.startingComment("starting")
				.endingComment("ending")
				.build();
	}

}

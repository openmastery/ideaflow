/**
 * Copyright 2015 New Iron Group, Inc.
 * <p/>
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.gnu.org/licenses/gpl-3.0.en.html
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ideaflow.publisher.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.ideaflow.publisher.api.BandStart;
import org.ideaflow.publisher.api.Message;
import org.ideaflow.publisher.api.ResourcePaths;
import org.springframework.stereotype.Component;

@Component
@Path(ResourcePaths.TASK_PATH + "/{taskId}" + ResourcePaths.EVENT_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

	@POST
	@Path(ResourcePaths.CONFLICT_PATH + ResourcePaths.START_PATH)
	public void startConflict(@PathParam("taskId") String taskId, String question) {
		System.out.println("Start Conflict: " + taskId + ", " + question);
	}

	@POST
	@Path(ResourcePaths.CONFLICT_PATH + ResourcePaths.STOP_PATH)
	public void stopConflict(@PathParam("taskId") String taskId, String resolution) {
		System.out.println("Stop Conflict: " + taskId + ", " + resolution);
	}

	@POST
	@Path(ResourcePaths.LEARNING_PATH + ResourcePaths.START_PATH)
	public void startLearning(@PathParam("taskId") String taskId, BandStart bandStart) {
		System.out.println("Start Learning: " + taskId + ", " + bandStart);
	}

	@POST
	@Path(ResourcePaths.LEARNING_PATH + ResourcePaths.STOP_PATH)
	public void stopLearning(@PathParam("taskId") String taskId) {
		System.out.println("Stop Learning: " + taskId);
	}

	@POST
	@Path(ResourcePaths.REWORK_PATH + ResourcePaths.START_PATH)
	public void startRework(@PathParam("taskId") String taskId, BandStart bandStart) {
		System.out.println("Start Rework: " + taskId + ", " + bandStart);
	}

	@POST
	@Path(ResourcePaths.REWORK_PATH + ResourcePaths.STOP_PATH)
	public void stopRework(@PathParam("taskId") String taskId) {
		System.out.println("Stop Rework: " + taskId);
	}

	@POST
	@Path(ResourcePaths.NOTE_PATH)
	public void addNote(@PathParam("taskId") String taskId, Message message) {
		System.out.println("Add Note: " + taskId + ", " + message);
	}

	@POST
	@Path(ResourcePaths.COMMIT_PATH)
	public void addCommit(@PathParam("taskId") String taskId, Message message) {
		System.out.println("Add Commit: " + taskId + ", " + message);
	}

}

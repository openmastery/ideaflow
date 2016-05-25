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
package org.ideaflow.publisher.client;

import org.openmastery.rest.client.CrudClient;
import org.ideaflow.publisher.api.ideaflow.IdeaFlowState;
import org.ideaflow.publisher.api.ideaflow.IdeaFlowStateTransition;
import org.ideaflow.publisher.api.ResourcePaths;

public class IdeaFlowClient extends CrudClient<IdeaFlowStateTransition, IdeaFlowClient> {

	public IdeaFlowClient(String hostUri) {
		super(hostUri, ResourcePaths.IDEAFLOW_PATH, IdeaFlowStateTransition.class);
	}

	public IdeaFlowState getActiveState(Long taskId) {
		return (IdeaFlowState) getUntypedCrudClientRequest()
				.path(ResourcePaths.ACTIVE_STATE_PATH)
				.path(taskId)
				.entity(IdeaFlowState.class)
				.find();
	}

	private void startBand(Long taskId, String startingComment, String bandPath) {
		IdeaFlowStateTransition transition = IdeaFlowStateTransition.builder()
				.taskId(taskId)
				.comment(startingComment)
		        .build();

		crudClientRequest.path(bandPath)
				.path(ResourcePaths.START_PATH)
				.createWithPost(transition);
	}

	private void endBand(Long taskId, String endingComment, String bandPath) {
		IdeaFlowStateTransition transition = IdeaFlowStateTransition.builder()
				.taskId(taskId)
				.comment(endingComment)
		        .build();

		crudClientRequest.path(bandPath)
				.path(ResourcePaths.STOP_PATH)
				.createWithPost(transition);
	}

	public void startConflict(Long taskId, String question) {
		startBand(taskId, question, ResourcePaths.CONFLICT_PATH);
	}

	public void endConflict(Long taskId, String resolution) {
		endBand(taskId, resolution, ResourcePaths.CONFLICT_PATH);
	}

	public void startLearning(Long taskId, String comment) {
		startBand(taskId, comment, ResourcePaths.LEARNING_PATH);
	}

	public void endLearning(Long taskId, String comment) {
		endBand(taskId, comment, ResourcePaths.LEARNING_PATH);
	}

	public void startRework(Long taskId, String comment) {
		startBand(taskId, comment, ResourcePaths.REWORK_PATH);
	}

	public void endRework(Long taskId, String comment) {
		endBand(taskId, comment, ResourcePaths.REWORK_PATH);
	}

}

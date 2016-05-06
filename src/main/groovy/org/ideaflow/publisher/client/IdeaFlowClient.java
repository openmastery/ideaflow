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

import org.ideaflow.common.rest.client.CrudClient;
import org.ideaflow.common.rest.client.CrudClientRequest;
import org.ideaflow.publisher.api.ResourcePaths;

public class IdeaFlowClient extends CrudClient<Object, IdeaFlowClient> {

	public IdeaFlowClient(String hostUri) {
		super(hostUri, ResourcePaths.TASK_PATH, Object.class);
	}

	public void startConflict(String taskId, String question) {
		crudClientRequest.path(taskId)
				.path(ResourcePaths.IDEAFLOW_PATH)
				.path(ResourcePaths.CONFLICT_PATH)
				.path(ResourcePaths.START_PATH)
				.createWithPost(question);
	}

	public void stopConflict(String taskId, String resolution) {
		crudClientRequest.path(taskId)
				.path(ResourcePaths.IDEAFLOW_PATH)
				.path(ResourcePaths.CONFLICT_PATH)
				.path(ResourcePaths.STOP_PATH)
				.createWithPost(resolution);
	}

	public void startLearning(String taskId, String comment) {
		startBand(taskId, comment, ResourcePaths.LEARNING_PATH);
	}

	public void stopLearning(String taskId) {
		stopBand(taskId, ResourcePaths.LEARNING_PATH);
	}

	private void startBand(String taskId, String comment, String bandPath) {
		crudClientRequest.path(taskId)
				.path(ResourcePaths.IDEAFLOW_PATH)
				.path(bandPath)
				.path(ResourcePaths.START_PATH)
				.createWithPost(comment);
	}

	private void stopBand(String taskId, String bandPath) {
		crudClientRequest.path(taskId)
				.path(ResourcePaths.IDEAFLOW_PATH)
				.path(bandPath)
				.path(ResourcePaths.STOP_PATH)
				.createWithPost(null);
	}

	public void startRework(String taskId, String comment) {
		startBand(taskId, comment, ResourcePaths.REWORK_PATH);
	}

	public void stopRework(String taskId) {
		stopBand(taskId, ResourcePaths.REWORK_PATH);
	}

}

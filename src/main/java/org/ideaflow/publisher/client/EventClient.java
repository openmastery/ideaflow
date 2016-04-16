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
package org.ideaflow.publisher.client;

import org.ideaflow.publisher.api.Message;
import org.ideaflow.publisher.api.ResourcePaths;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class EventClient {

	private String resourceUri;
	private RestTemplate restTemplate;

	public EventClient(String hostUri) {
		resourceUri = hostUri + ResourcePaths.TASK_PATH;
		restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
	}

	private String getResourcePath(String taskId) {
		return resourceUri + "/" + taskId + ResourcePaths.EVENT_PATH;
	}

	public void startConflict(String taskId, String question) {
		String path = getResourcePath(taskId) + ResourcePaths.CONFLICT_PATH + ResourcePaths.START_PATH;
		restTemplate.postForLocation(path, question);
	}

	public void stopConflict(String taskId, String resolution) {
		String path = getResourcePath(taskId) + ResourcePaths.CONFLICT_PATH + ResourcePaths.STOP_PATH;
		restTemplate.postForLocation(path, resolution);
	}

	public void startLearning(String taskId, String comment) {
		startBand(taskId, comment, ResourcePaths.LEARNING_PATH);
	}

	public void stopLearning(String taskId) {
		stopBand(taskId, ResourcePaths.LEARNING_PATH);
	}

	private void startBand(String taskId, String comment, String bandPath) {
		String path = getResourcePath(taskId) + bandPath + ResourcePaths.START_PATH;
		restTemplate.postForLocation(path, comment);
	}

	private void stopBand(String taskId, String bandPath) {
		String path = getResourcePath(taskId) + bandPath + ResourcePaths.STOP_PATH;
		restTemplate.postForLocation(path, null);
	}

	public void startRework(String taskId, String comment) {
		startBand(taskId, comment, ResourcePaths.REWORK_PATH);
	}

	public void stopRework(String taskId) {
		stopBand(taskId, ResourcePaths.REWORK_PATH);
	}

	public void addNote(String taskId, String note) {
		addMessage(taskId, ResourcePaths.NOTE_PATH, note);
	}

	public void addCommit(String taskId, String message) {
		addMessage(taskId, ResourcePaths.COMMIT_PATH, message);
	}

	private void addMessage(String taskId, String messagePath, String content) {
		String path = getResourcePath(taskId) + messagePath;
		Message message = new Message(content);
		restTemplate.postForLocation(path, message);
	}

}

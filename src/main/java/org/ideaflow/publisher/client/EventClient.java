/**
 * Copyright 2015 New Iron Group, Inc.
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
package org.ideaflow.publisher.client;

import org.ideaflow.publisher.api.BandStart;
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

    private String getResourcePath(String taskName) {
        return resourceUri + "/" + taskName + ResourcePaths.EVENT_PATH;
    }

	public void startConflict(String taskName, String question) {
		String path = getResourcePath(taskName) + ResourcePaths.CONFLICT_PATH + ResourcePaths.START_PATH;
        restTemplate.postForLocation(path, question);
	}

	public void stopConflict(String taskName, String resolution) {
		String path = getResourcePath(taskName) + ResourcePaths.CONFLICT_PATH + ResourcePaths.STOP_PATH;
        restTemplate.postForLocation(path, resolution);
	}

    public void startLearning(String taskName, String comment) {
        startBand(taskName, comment, ResourcePaths.LEARNING_PATH);
    }

    public void stopLearning(String taskName) {
        stopBand(taskName, ResourcePaths.LEARNING_PATH);
    }

    private void startBand(String taskName, String comment, String bandPath) {
        String path = getResourcePath(taskName) + bandPath + ResourcePaths.START_PATH;
        BandStart bandStart = new BandStart(comment);
        restTemplate.postForLocation(path, bandStart);
    }

    private void stopBand(String taskName, String bandPath) {
        String path = getResourcePath(taskName) + bandPath + ResourcePaths.STOP_PATH;
        restTemplate.postForLocation(path, null);
    }

    public void startRework(String taskName, String comment) {
        startBand(taskName, comment, ResourcePaths.REWORK_PATH);
    }

    public void stopRework(String taskName) {
        stopBand(taskName, ResourcePaths.REWORK_PATH);
    }

    public void addNote(String taskName, String note) {
        addMessage(taskName, ResourcePaths.NOTE_PATH, note);
    }

    public void addCommit(String taskName, String message) {
        addMessage(taskName, ResourcePaths.COMMIT_PATH, message);
    }

    private void addMessage(String taskName, String messagePath, String content) {
        String path = getResourcePath(taskName) + messagePath;
        Message message = new Message(content);
        restTemplate.postForLocation(path, message);
    }

}

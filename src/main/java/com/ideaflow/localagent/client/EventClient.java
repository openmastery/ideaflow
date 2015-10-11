package com.ideaflow.localagent.client;

import com.ideaflow.localagent.api.BandStart;
import com.ideaflow.localagent.api.ConflictEnd;
import com.ideaflow.localagent.api.ConflictStart;
import com.ideaflow.localagent.api.Message;
import com.ideaflow.localagent.api.ResourcePaths;
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
        ConflictStart conflictStart = new ConflictStart(question);
        restTemplate.postForLocation(path, conflictStart);
	}

	public void stopConflict(String taskName, String resolution) {
		String path = getResourcePath(taskName) + ResourcePaths.CONFLICT_PATH + ResourcePaths.STOP_PATH;
        ConflictEnd conflictEnd = new ConflictEnd(resolution);
        restTemplate.postForLocation(path, conflictEnd);
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

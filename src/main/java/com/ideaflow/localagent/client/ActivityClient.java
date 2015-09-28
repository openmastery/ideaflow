package com.ideaflow.localagent.client;

import com.ideaflow.localagent.api.ResourcePaths;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class ActivityClient {

    private String resourceUri;
    private RestTemplate restTemplate;

    public ActivityClient(String hostUri) {
        resourceUri = hostUri + ResourcePaths.ACTIVITY_PATH;
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
    }

	public void startConflict(String taskId) {
		String path = resourceUri.concat("/" + taskId).concat(ResourcePaths.CONFLICT_PATH).concat(ResourcePaths.START_PATH);
		restTemplate.put(path, null);
	}

	public void stopConflict(String taskId) {
		String path = resourceUri.concat("/" + taskId).concat(ResourcePaths.CONFLICT_PATH).concat(ResourcePaths.STOP_PATH);
		restTemplate.put(path, null);
	}

}

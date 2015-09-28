package com.ideaflow.localagent.client;

import com.ideaflow.localagent.api.ResourcePaths;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class EventClient {

	private String resourceUri;
	private RestTemplate restTemplate;

	public EventClient(String hostUri) {
		resourceUri = hostUri + ResourcePaths.EVENT_PATH;
		restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
	}

	public void startConflict() {
		String path = resourceUri.concat(ResourcePaths.CONFLICT_PATH).concat(ResourcePaths.START_PATH);
        restTemplate.postForLocation(path, null);
	}

	public void stopConflict() {
		String path = resourceUri.concat(ResourcePaths.CONFLICT_PATH).concat(ResourcePaths.STOP_PATH);
        restTemplate.postForLocation(path, null);
	}

}

package org.openmastery.publisher.client;

import org.openmastery.publisher.api.ResourcePaths;

public class UserClient extends IdeaFlowClient<String, UserClient> {

	public UserClient(String hostUri) {
		super(hostUri, ResourcePaths.USER_PATH, String.class);
	}

	public String getBearerToken(String apiKey) {
		return crudClientRequest.path(ResourcePaths.BEARER_TOKEN_PATH)
				.queryParam("apiKey", apiKey)
				.find();
	}

}

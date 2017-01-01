package org.openmastery.storyweb.client;

import com.bancvue.rest.client.ClientRequestFactory;
import com.bancvue.rest.client.crud.CrudClient;
import com.bancvue.rest.client.crud.CrudClientRequest;
import org.openmastery.storyweb.api.ResourcePaths;

public abstract class StorywebClient<API_TYPE, CLIENT_TYPE extends CrudClient> extends CrudClient<API_TYPE, CLIENT_TYPE> {

	public StorywebClient(String baseUrl, String path, Class<API_TYPE> type) {
		super(baseUrl, path, type);
	}

	public StorywebClient(ClientRequestFactory clientRequestFactory, String baseUrl, String path, Class<API_TYPE> type) {
		super(clientRequestFactory, baseUrl, path, type);
	}

	public StorywebClient(CrudClientRequest<API_TYPE> crudClientRequest) {
		super(crudClientRequest);
	}

	public CLIENT_TYPE apiKey(String apiKey) {
		return header(ResourcePaths.API_KEY_HEADER, apiKey);
	}

}

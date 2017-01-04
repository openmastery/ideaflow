package org.openmastery.publisher.client;

import com.bancvue.rest.client.ClientRequestFactory;
import com.bancvue.rest.client.crud.CrudClient;
import com.bancvue.rest.client.crud.CrudClientRequest;
import org.glassfish.jersey.client.ClientProperties;
import org.openmastery.publisher.api.ResourcePaths;

public abstract class OpenMasteryClient<API_TYPE, CLIENT_TYPE extends CrudClient> extends CrudClient<API_TYPE, CLIENT_TYPE> {

	public OpenMasteryClient(String baseUrl, String path, Class<API_TYPE> type) {
		super(baseUrl, path, type);
	}

	public OpenMasteryClient(ClientRequestFactory clientRequestFactory, String baseUrl, String path, Class<API_TYPE> type) {
		super(clientRequestFactory, baseUrl, path, type);
	}

	public OpenMasteryClient(CrudClientRequest<API_TYPE> crudClientRequest) {
		super(crudClientRequest);
	}

	public CLIENT_TYPE apiKey(String apiKey) {
		return header(ResourcePaths.API_KEY_HEADER, apiKey);
	}

	public CLIENT_TYPE readTimeout(int readTimeoutMillis) {
		return property(ClientProperties.READ_TIMEOUT, Integer.toString(readTimeoutMillis));
	}

	public CLIENT_TYPE connectTimeout(int connectTimeoutMillis) {
		return property(ClientProperties.CONNECT_TIMEOUT, Integer.toString(connectTimeoutMillis));
	}

}

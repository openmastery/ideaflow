package org.openmastery.storyweb.client;

import com.bancvue.rest.client.crud.CrudClientRequest;
import org.openmastery.storyweb.api.FaqSummary;
import org.openmastery.storyweb.api.ResourcePaths;

import java.util.List;

public class FaqClient extends StorywebClient<FaqSummary, FaqClient> {

	public FaqClient(String baseUrl) {
		super(baseUrl, ResourcePaths.FAQ_PATH, FaqSummary.class);
	}

	public List<FaqSummary> findAllFaqMatchingCriteria(List<String> tags) {
		CrudClientRequest<FaqSummary> findAllRequest = crudClientRequest;

		for (String tag : tags) {
			findAllRequest = findAllRequest.queryParam("tag", tag);
		}

		return findAllRequest.findMany();
	}

}

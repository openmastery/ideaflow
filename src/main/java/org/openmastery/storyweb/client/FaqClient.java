package org.openmastery.storyweb.client;

import com.bancvue.rest.client.crud.CrudClientRequest;
import com.bancvue.rest.client.crud.GenericTypeFactory;
import org.openmastery.publisher.api.PagedResult;
import org.openmastery.publisher.api.task.Task;
import org.openmastery.storyweb.api.FaqSummary;
import org.openmastery.storyweb.api.PainPoint;
import org.openmastery.storyweb.api.ResourcePaths;

import javax.ws.rs.core.GenericType;
import java.lang.reflect.Field;
import java.util.List;

public class FaqClient extends StorywebClient<PainPoint, FaqClient> {

	public FaqClient(String baseUrl) {
		super(baseUrl, ResourcePaths.STORY_WEB_PATH + ResourcePaths.FAQ_PATH, PainPoint.class);
	}

	public PagedResult<PainPoint> findAllFaqMatchingCriteria(List<String> tags, Integer pageNumber, Integer perPage) {
		CrudClientRequest request = getUntypedCrudClientRequest()
				.queryParam("page_number", pageNumber)
				.queryParam("per_page", perPage);

		for (String tag : tags) {
			request = request.queryParam("tag", tag);
		}

		return (PagedResult<PainPoint>) withPagedResultType(request).find();
	}

	private static final GenericTypeFactory GENERIC_TYPE_FACTORY = GenericTypeFactory.getInstance();

	private CrudClientRequest withPagedResultType(CrudClientRequest request) {
		GenericType<PagedResult<PainPoint>> entityType = GENERIC_TYPE_FACTORY.createGenericType(PagedResult.class, PainPoint.class);
		try {
			Field entityField = request.getClass().getDeclaredField("entity");
			entityField.setAccessible(true);
			entityField.set(request, entityType);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return request;
	}

}

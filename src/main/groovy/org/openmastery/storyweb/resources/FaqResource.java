/**
 * Copyright 2017 New Iron Group, Inc.
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
package org.openmastery.storyweb.resources;


import org.openmastery.publisher.api.PagedResult;
import org.openmastery.storyweb.api.StoryPoint;
import org.openmastery.storyweb.api.ResourcePaths;
import org.openmastery.storyweb.core.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.STORY_WEB_PATH + ResourcePaths.FAQ_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class FaqResource {


	@Autowired
	MetricsService metricsService;

	/**
	 * Returns the most painful points in the StoryWeb, optionally filterable by tag
	 * @param tags Can be contextTags, painTags or a mix of both
	 * @return List<StoryPoint>
	 */

	@GET
	public PagedResult<StoryPoint> findPainfulStoryPoints(@QueryParam("project") String project,
												  @DefaultValue("0") @QueryParam("page_number") Integer pageNumber,
												  @DefaultValue("10") @QueryParam("per_page") Integer elementsPerPage,
												  @QueryParam("tag") List<String> tags) {

		List<StoryPoint> painPoints = metricsService.findAndFilterBiggestPainPoints(tags);

		PagedResult<StoryPoint> pagedResult = PagedResult.create(painPoints.size(), pageNumber, elementsPerPage);
		int firstIndex = pageNumber * elementsPerPage;

		int lastIndex = (pageNumber + 1) * elementsPerPage;
		if (lastIndex > painPoints.size()) {
			lastIndex = painPoints.size();
		}
		pagedResult.setContents(painPoints.subList(firstIndex, lastIndex));

		return pagedResult;
	}




}

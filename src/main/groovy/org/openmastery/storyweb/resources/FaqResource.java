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


import org.openmastery.storyweb.api.FaqSummary;
import org.openmastery.storyweb.api.ResourcePaths;
import org.openmastery.storyweb.core.StoryWebService;
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
	StoryWebService persistenceService;



	//TODO needs to POST FAQ description to specific URL

	/**
	 * Retrieves FaqSummary that include the specific FAQ notes found,
	 * along with a referencable link to a task, and a path within a task
	 *
	 * @param tags tags to search for (without #)
	 * @return List<FaqSummary>
	 */
	@GET
	public List<FaqSummary> findAllFaqMatchingCriteria(@QueryParam("tag") List<String> tags) {

		//TODO needs to return everything with FAQ notes when there's no tags present
		return persistenceService.findAllFaqMatchingTags(tags);
	}

	//@POST
//	public List<FaqSummary> findAllFaqMatchingCriteria(@QueryParam("tag") List<String> tags) {
//
//		return persistenceService.findAllFaqMatchingTags(tags);
//	}


}

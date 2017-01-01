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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.FAQ_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class FaqResource {

	@Autowired
	StoryWebService persistenceService;


	/**
	 * Retrieves IFMs with specific event pointers that include either FAQ notes
	 * or event comments that with at least one of the specified tags.
	 *
	 * @param tags tags to search for (without #)
	 * @return List<FaqSummary>
	 */
	@GET
	public List<FaqSummary> findAllFaqMatchingCriteria(@QueryParam("tag") List<String> tags) {

		return persistenceService.findAllFaqMatchingCriteria(tags);
	}

}

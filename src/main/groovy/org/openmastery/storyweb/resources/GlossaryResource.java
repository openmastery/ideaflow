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

import lombok.extern.slf4j.Slf4j;
import org.openmastery.storyweb.api.GlossaryDefinition;
import org.openmastery.storyweb.api.ResourcePaths;
import org.openmastery.storyweb.core.StoryWebService;
import org.openmastery.storyweb.core.glossary.GlossaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.GLOSSARY_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class GlossaryResource {

	@Autowired
	private StoryWebService storyWebService;

	@Autowired
	private GlossaryRepository glossaryRepository;

	/**
	 * Create or update a single glossary definition
	 * @param glossaryDefinition provide a definition for the term
	 * @return GlossaryDefinition
	 */
	@PUT
	public GlossaryDefinition createOrUpdateSingle(GlossaryDefinition glossaryDefinition) {
		return storyWebService.createOrUpdateGlossaryDefinition(glossaryDefinition);

	}

	/**
	 * Make sure all the provided tags are available in the glossary,
	 * if not, create new blank definitions
	 *
	 * @param tags List of tags
	 */

	@POST
	@Path(ResourcePaths.TAG_PATH)
	public void createBlankGlossaryDefinitionWhenNotExists(List<String> tags) {
		storyWebService.createGlossaryDefinitionsWhenNotExists(tags);
	}


	/**
	 * Retrieve all glossary definitions, or if tags are specified, filter by tags
	 * @param tags Return only the specified tags
	 * @return List<GlossaryDefinition>
	 */
	@GET
	public List<GlossaryDefinition> findGlossaryDefinitionsByTag(@QueryParam("tag") List<String> tags) {
		log.debug("findGlossaryDefinitionsByTag : " + tags);
		if (tags == null || tags.isEmpty()) {
			return storyWebService.findAllGlossaryDefinitions();
		} else {
			return storyWebService.findGlossaryDefinitionsByTag(tags);
		}
	}

}

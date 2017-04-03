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
import org.openmastery.storyweb.api.glossary.Glossary;
import org.openmastery.storyweb.api.glossary.GlossaryDefinition;
import org.openmastery.storyweb.api.ResourcePaths;
import org.openmastery.storyweb.core.glossary.GlossaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.STORY_WEB_PATH + ResourcePaths.GLOSSARY_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class GlossaryResource {

	@Autowired
	private GlossaryService glossaryService;

	/**
	 * Update an existing glossary definition
	 * @param glossaryDefinition update the definition for the term
	 * @return GlossaryDefinition
	 */
	@PUT
	@Path(ResourcePaths.GLOSSARY_TERM_PATH + ResourcePaths.ID_PATH + "/{tagId}")
	public GlossaryDefinition updateExistingTerm(@PathParam("tagId") Long tagId, GlossaryDefinition glossaryDefinition) {
		return glossaryService.updateExistingTerm(tagId, glossaryDefinition);
	}

	/**
	 * Create a new glossary definition
	 * @param glossaryDefinition provide a definition for the term
	 * @return GlossaryDefinition
	 */
	@POST
	@Path(ResourcePaths.GLOSSARY_TERM_PATH)
	public GlossaryDefinition createNewTerm(GlossaryDefinition glossaryDefinition) {
		return glossaryService.createNewTerm(glossaryDefinition);
	}


	/**
	 * Make sure all the provided tags are available in the glossary,
	 * if not, create new blank definitions
	 *
	 * @param tags List of tags
	 */

	@POST
	@Path(ResourcePaths.GLOSSARY_BLANK_PATH)
	public Glossary createEmptyGlossaryDefinitionsWhenNotExists(List<String> tags) {

		Glossary glossary = glossaryService.createGlossaryDefinitionsWhenNotExists(tags);
		System.out.println(glossary);
		return glossary;
	}


	/**
	 * Retrieve all glossary definitions, or if tags are specified, filter by tags
	 * @param tags Return only the specified tags
	 * @return Glossary
	 */
	@GET
	public Glossary findGlossaryDefinitionsByTag(@QueryParam("tag") List<String> tags) {
		if (tags == null || tags.isEmpty()) {
			return glossaryService.findAllGlossaryDefinitions();
		} else {
			return glossaryService.findGlossaryDefinitionsByTag(tags);
		}
	}

	/**
	 * Retrieve all glossary definitions mentioned by the associated task
	 * @param taskId Return only the terms tagged within the task
	 * @return Glossary
	 */
	@GET
	@Path(ResourcePaths.TASK_PATH + "/{taskId}")
	public Glossary findGlossaryDefinitionsByTask(@PathParam("taskId") Long taskId) {
			return glossaryService.findAllGlossaryDefinitionsByTask(taskId);
	}

}

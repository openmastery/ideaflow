package org.openmastery.storyweb.client;

import com.bancvue.rest.client.crud.CrudClientRequest;
import org.openmastery.storyweb.api.glossary.Glossary;
import org.openmastery.storyweb.api.glossary.GlossaryDefinition;
import org.openmastery.storyweb.api.ResourcePaths;

import java.util.List;

public class GlossaryClient extends StorywebClient<GlossaryDefinition, GlossaryClient> {

	public GlossaryClient(String baseUrl) {
		super(baseUrl, ResourcePaths.STORY_WEB_PATH + ResourcePaths.GLOSSARY_PATH, GlossaryDefinition.class);
	}

	public GlossaryDefinition createNewTerm(String name, String description) {
		GlossaryDefinition entry = GlossaryDefinition.builder()
				.name(name)
				.description(description)
				.build();

		return crudClientRequest
				.path(ResourcePaths.GLOSSARY_TERM_PATH)
				.createWithPost(entry);

	}

	public GlossaryDefinition updateTerm(Long id, String name, String description) {
		GlossaryDefinition entry = GlossaryDefinition.builder()
				.name(name)
				.description(description)
				.build();

		return crudClientRequest
				.path(ResourcePaths.GLOSSARY_TERM_PATH)
				.path(ResourcePaths.ID_PATH).path(id)
				.updateWithPut(entry);
	}

	public Glossary findAllDefinitions() {
		return (Glossary) getUntypedCrudClientRequest()
				.entity(Glossary.class)
				.find();
	}

	public Glossary findAllDefinitionsByTask(Long taskId) {
		return (Glossary) getUntypedCrudClientRequest()
				.entity(Glossary.class)
				.path(ResourcePaths.TASK_PATH)
				.path(taskId)
				.find();
	}

	public Glossary findDefinitionsbyTag(List<String> tags) {
		CrudClientRequest<Glossary> findFilteredRequest = getUntypedCrudClientRequest();

		for (String tag : tags) {
			findFilteredRequest = findFilteredRequest.queryParam("tag", tag);
		}

		return findFilteredRequest.entity(Glossary.class)
				.find();

	}

	public Glossary createBlankGlossaryDefinitionWhenNotExists(List<String> tags) {
		return (Glossary) getUntypedCrudClientRequest()
				.path(ResourcePaths.GLOSSARY_BLANK_PATH)
				.entity(Glossary.class)
				.createWithPost(tags);
	}

}

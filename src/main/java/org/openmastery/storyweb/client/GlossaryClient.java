package org.openmastery.storyweb.client;

import com.bancvue.rest.client.crud.CrudClientRequest;
import lombok.extern.slf4j.Slf4j;
import org.openmastery.storyweb.api.FaqSummary;
import org.openmastery.storyweb.api.GlossaryDefinition;
import org.openmastery.storyweb.api.ResourcePaths;

import java.util.List;

@Slf4j
public class GlossaryClient extends StorywebClient<GlossaryDefinition, GlossaryClient> {

	public GlossaryClient(String baseUrl) {
		super(baseUrl, ResourcePaths.GLOSSARY_PATH, GlossaryDefinition.class);
	}

	public void defineTag(String name, String description) {
		GlossaryDefinition entry = GlossaryDefinition.builder()
				.name(name)
				.description(description)
				.build();

		crudClientRequest.updateWithPut(entry);
	}

	public List<GlossaryDefinition> findAllDefinitions() {
		return crudClientRequest.findMany();
	}

	public List<GlossaryDefinition> findDefinitionsbyTag(List<String> tags) {
		CrudClientRequest<GlossaryDefinition> findFilteredRequest = crudClientRequest;

		log.debug("Client request: findDefinitionsbyTag "+tags);
		for (String tag : tags) {
			findFilteredRequest = findFilteredRequest.queryParam("tag", tag);
		}

		return findFilteredRequest.findMany();

	}

	public void createBlankGlossaryDefinitionWhenNotExists(List<String> tags) {
		crudClientRequest.path(ResourcePaths.TAG_PATH).createWithPost(tags);
	}

}

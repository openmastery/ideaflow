package org.openmastery.storyweb.client;

import org.openmastery.storyweb.api.GlossaryEntry;
import org.openmastery.storyweb.api.ResourcePaths;

import java.util.List;


public class GlossaryClient extends StorywebClient<GlossaryEntry, GlossaryClient> {

	public GlossaryClient(String baseUrl) {
		super(baseUrl, ResourcePaths.GLOSSARY_PATH, GlossaryEntry.class);
	}

	public void addEntry(String name, String description) {
		GlossaryEntry entry = GlossaryEntry.builder()
				.name(name)
				.description(description)
				.build();

		crudClientRequest.updateWithPut(entry);
	}

	public List<GlossaryEntry> findAllEntries() {
		return crudClientRequest.findMany();
	}

}

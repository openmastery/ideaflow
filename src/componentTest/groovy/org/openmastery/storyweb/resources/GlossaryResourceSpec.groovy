package org.openmastery.storyweb.resources

import org.openmastery.publisher.ComponentTest
import org.openmastery.storyweb.api.GlossaryDefinition
import org.openmastery.storyweb.client.GlossaryClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class GlossaryResourceSpec extends Specification {

	@Autowired
	private GlossaryClient glossaryClient

	def "defineTag SHOULD create entry"() {
		given:
		glossaryClient.defineTag("#tag", "tag description")

		when:
		List<GlossaryDefinition> entries = glossaryClient.findAllDefinitions()

		then:
		assert entries == [new GlossaryDefinition("#tag", "tag description")]
	}

	def "findEntriesByTag SHOULD filter entries by tag"() {
		given:
		glossaryClient.defineTag("#tag1", "entry description")
		glossaryClient.defineTag("#tag2", "entry description")


		when:
		List<GlossaryDefinition> entries = glossaryClient.findDefinitionsbyTag(["#tag2"])

		then:
		assert entries == [new GlossaryDefinition("#tag2", "entry description")]
	}

	def "createBlankGlossaryDefinitionWhenNotExists SHOULD NOT override existing definitions"() {
		given:
		glossaryClient.defineTag("#tag1", "entry description")
		glossaryClient.defineTag("#tag2", "entry description")


		when:
		glossaryClient.createBlankGlossaryDefinitionWhenNotExists(["#tag2", "#tag3"])
		List<GlossaryDefinition> entries = glossaryClient.findAllDefinitions()

		then:
		assert entries.size() == 3
		assert entries.get(0).name == "#tag1"
		assert entries.get(0).description == "entry description"
		assert entries.get(1).name == "#tag2"
		assert entries.get(1).description == "entry description"
		assert entries.get(2).name == "#tag3"
		assert entries.get(2).description == null
	}


}

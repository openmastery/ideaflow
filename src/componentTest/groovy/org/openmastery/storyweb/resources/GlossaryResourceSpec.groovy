package org.openmastery.storyweb.resources

import com.bancvue.rest.exception.ConflictException
import com.bancvue.rest.exception.NotFoundException
import org.openmastery.publisher.ComponentTest
import org.openmastery.storyweb.api.GlossaryDefinition
import org.openmastery.storyweb.client.GlossaryClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class GlossaryResourceSpec extends Specification {

	@Autowired
	private GlossaryClient glossaryClient

	def "createNewTerm SHOULD create entry"() {
		given:
		glossaryClient.createNewTerm("#tag", "tag description")

		when:
		List<GlossaryDefinition> entries = glossaryClient.findAllDefinitions()

		then:
		assert entries.size() == 1
		assert entries.get(0).name == '#tag'
		assert entries.get(0).description == "tag description"

	}

	def "createNewTerm SHOULD explode nicely when term already exists"() {
		when:
		glossaryClient.createNewTerm("#tag", "tag description")
		glossaryClient.createNewTerm("#TAG", "tag description")

		then:
		thrown(ConflictException)
	}

	def "createNewTerm SHOULD explode nicely regardless of whether # is included"() {
		when:
		glossaryClient.createNewTerm("tag", "tag description")
		glossaryClient.createNewTerm("#TAG", "tag description")

		then:
		thrown(ConflictException)
	}

	def "updateTerm SHOULD update name and description"() {
		given:
		GlossaryDefinition definition = glossaryClient.createNewTerm("#tag", "tag description")

		when:
		glossaryClient.updateTerm(definition.id, "#TAG", "updated")
		List<GlossaryDefinition> entries = glossaryClient.findAllDefinitions()

		then:
		assert entries.size() == 1
		assert entries.get(0).name == '#TAG'
		assert entries.get(0).description == "updated"
	}

	def "updateTerm SHOULD explode nicely when term not found"() {
		when:
		glossaryClient.updateTerm(-1, "#TAG", "updated")

		then:
		thrown(NotFoundException)
	}


	def "findDefinitionsByTag SHOULD filter entries by tag"() {
		given:
		glossaryClient.createNewTerm("#tag1", "entry description1")
		glossaryClient.createNewTerm("#tag2", "entry description2")


		when:
		List<GlossaryDefinition> entries = glossaryClient.findDefinitionsbyTag(["#tag2"])

		then:
		assert entries.size() == 1
		assert entries.get(0).name == '#tag2'
		assert entries.get(0).description == "entry description2"

	}

	def "createBlankGlossaryDefinitionWhenNotExists SHOULD NOT override existing definitions"() {
		given:
		glossaryClient.createNewTerm("#tag1", "entry description")
		glossaryClient.createNewTerm("#tag2", "entry description")


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

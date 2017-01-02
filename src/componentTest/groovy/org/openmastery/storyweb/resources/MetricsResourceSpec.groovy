package org.openmastery.storyweb.resources

import org.openmastery.publisher.ComponentTest
import org.openmastery.storyweb.api.GlossaryDefinition
import org.openmastery.storyweb.client.GlossaryClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class MetricsResourceSpec extends Specification {


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

	def "generateSPCChart SHOULD "() {

	}
}

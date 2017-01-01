package org.openmastery.storyweb.resources

import org.openmastery.publisher.ComponentTest
import org.openmastery.storyweb.api.GlossaryEntry
import org.openmastery.storyweb.client.GlossaryClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class GlossaryResourceSpec extends Specification {

	@Autowired
	private GlossaryClient glossaryClient

	def "should create entry"() {
		given:
		glossaryClient.addEntry("entry-name", "entry description")

		when:
		List<GlossaryEntry> entries = glossaryClient.findAllEntries()

		then:
		assert entries == [new GlossaryEntry("entry-name", "entry description")]
	}

}

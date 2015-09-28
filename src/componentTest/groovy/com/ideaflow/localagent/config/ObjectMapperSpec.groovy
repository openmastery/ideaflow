package com.ideaflow.localagent.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.ideaflow.localagent.ComponentTest
import java.time.LocalDate
import spock.lang.Specification

@ComponentTest
class ObjectMapperSpec extends Specification {

	private ObjectMapper mapper = new ObjectMapperContextResolver().getContext(Object)

	def "should serialize LocalDate"() {
		given: "A bean with a LocalDate data member"
		TestBean testBean = new TestBean()
		testBean.thedate = LocalDate.of(2014, 7, 6)

		when:
		String json = mapper.writeValueAsString(testBean)

		then:
		json == '{"thedate":"2014-07-06"}'
	}

	def "should deserialize LocalDate"() {
		given: "A properly formatted JSON string"
		def json = '{"thedate":"2014-07-06"}'

		when:
		TestBean testBean = mapper.readValue(json, TestBean)

		then:
		testBean.thedate == LocalDate.of(2014, 7, 6)
	}

	public static class TestBean {
		public LocalDate thedate
	}
}

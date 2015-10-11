/*
 * Copyright 2015 New Iron Group, Inc.
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

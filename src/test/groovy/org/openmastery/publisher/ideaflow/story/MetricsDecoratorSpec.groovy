package org.openmastery.publisher.ideaflow.story

import spock.lang.Specification


class MetricsDecoratorSpec extends Specification {

	MetricsDecorator decorator = new MetricsDecorator()

	def "matchesPath SHOULD match if the path is to the specific metric"() {
		given:
		String metricPath = "/task/1/subtask/2/METRIC_NAME"
		String searchPath = "/task/1/subtask/2"

		when:
		boolean matches = decorator.matchesPath(searchPath, metricPath)

		then:
		assert matches == true
	}

	def "matchesPath SHOULD NOT match if the path is to a finer grained metric"() {
		given:
		String metricPath = "/task/1/subtask/2/journey/5/METRIC_NAME"
		String searchPath = "/task/1/subtask/2"

		when:
		boolean matches = decorator.matchesPath(searchPath, metricPath)

		then:
		assert matches == false
	}
}

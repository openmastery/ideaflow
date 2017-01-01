package org.openmastery.storyweb.resources

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.storyweb.api.FaqSummary
import org.openmastery.storyweb.client.FaqClient
import org.openmastery.time.TimeConverter
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class FaqResourceSpec extends Specification {

	@Autowired
	FaqClient faqClient

	@Autowired
	private IdeaFlowPersistenceService persistenceService


	//TODO please define all tests this way -

	// 1. "<the name of the behavior under test> (usually a method name)"
	// 2. SHOULD <characteristics you wish to observe>
	// 3. WHEN <the specific context (environment or state) in which the behavior occurs> (specify for any case other than "ALWAYS")

	def "findAllFaqMatchingCriteria SHOULD match the search criteria WHEN comment includes one or more tags"() {
		given:
		Long taskId = 312L;

		EventEntity event = aRandom.eventEntity().taskId(taskId).build()
		FaqAnnotationEntity annotation = aRandom.faqAnnotationEntity().taskId(taskId).comment("for #this and #that")build()

		EventEntity savedEvent = persistenceService.saveEvent(event)
		annotation.eventId = savedEvent.id
		persistenceService.saveAnnotation(annotation)

		when:

		List<FaqSummary> faqs = faqClient.findAllFaqMatchingCriteria(["this", "othertag"]);
		then:
		assert faqs.size() == 1
		assert faqs.get(0).taskId == event.taskId
		assert faqs.get(0).eventId == savedEvent.id
		assert faqs.get(0).position == TimeConverter.toJodaLocalDateTime(event.position)
		assert faqs.get(0).faqComment == annotation.comment
		assert faqs.get(0).eventComment == event.comment
		assert faqs.get(0).tags == ["#this", "#that"].toSet()

	}
}

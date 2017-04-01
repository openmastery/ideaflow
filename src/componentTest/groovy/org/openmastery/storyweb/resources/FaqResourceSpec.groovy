package org.openmastery.storyweb.resources

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.PagedResult
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.user.UserEntity
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.storyweb.api.StoryPoint
import org.openmastery.storyweb.client.FaqClient
import org.openmastery.storyweb.core.FixturePersistenceHelper
import org.openmastery.time.MockTimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class FaqResourceSpec extends Specification {

	@Autowired
	FaqClient faqClient

	@Autowired
	private UserEntity testUser;


	@Autowired
	private IdeaFlowPersistenceService persistenceService

	@Autowired
	private FixturePersistenceHelper fixturePersistenceHelper


	private long taskId

	MockTimeService mockTimeService = new MockTimeService()
	IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)


	def setup() {
		taskId = persistenceService.saveTask(
				aRandom.taskEntity().ownerId(testUser.id).build()).id
	}





	//TODO please define all tests this way -

	// 1. "<the name of the behavior under test> (usually a method name)"
	// 2. SHOULD <characteristics you wish to observe>
	// 3. WHEN <the specific context (environment or state) in which the behavior occurs> (specify for any case other than "ALWAYS")

	def "findAllFaqMatchingCriteria SHOULD match the search criteria WHEN comment includes one or more tags"() {
		given:

		builder.activate()
				.wtf()
				.advanceMinutes(30)
				.executeCode()
				.executeCode()
				.distraction()
				.executeCode()
				.advanceMinutes(1)
				.wtf("This is a comment #this")
				.advanceMinutes(30)
				.executeCode()
				.executeCode()
				.idleDays(1)
				.advanceMinutes(5)
				.awesome()
				.advanceMinutes(5)
				.deactivate()

		fixturePersistenceHelper.saveIdeaFlow(testUser.id, taskId, builder)

		when:

		PagedResult<StoryPoint> painPointResults = faqClient.findAllFaqMatchingCriteria(["this", "othertag"], null, null);
		then:
		assert painPointResults.contents.size() == 1
//		assert faqs.get(0).taskId == event.taskId
//		assert faqs.get(0).eventId == savedEvent.id
//		assert faqs.get(0).position == TimeConverter.toJodaLocalDateTime(event.position)
//		assert faqs.get(0).faqComment == annotation.comment
//		assert faqs.get(0).eventComment == event.comment
//		assert faqs.get(0).tags == ["#this", "#that"].toSet()

	}
}

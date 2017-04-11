package org.openmastery.publisher

import com.bancvue.rest.exception.NotFoundException
import org.openmastery.publisher.api.PagedResult
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.TaskClient
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.stub.FixtureDataGenerator
import org.openmastery.publisher.core.user.UserEntity
import org.openmastery.publisher.resources.TimelineResource
import org.openmastery.storyweb.client.FaqClient
import org.openmastery.storyweb.client.StorywebClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Specification


@ComponentTest
class FixtureTimelineValidationSpec extends Specification {

	@Value('http://localhost:${server.port}')
	private String hostUri;
	@Autowired
	private IdeaFlowPersistenceService persistenceService
	@Autowired
	private UserEntity userEntity
	@Autowired
	private TaskClient taskClient
	@Autowired
	private FaqClient faqClient
	@Autowired
	private TimelineResource timelineResource
	private FixtureDataGenerator fixtureDataGenerator = new FixtureDataGenerator()

	def setup() {
		fixtureDataGenerator.hostUri = hostUri
		fixtureDataGenerator.persistenceService = persistenceService
		fixtureDataGenerator.connect(userEntity.apiKey)
	}

	def "should not fail to retrieve timeline for fixture data set"() {
		given:
		fixtureDataGenerator.generateStubTasks()
		List<Task> recentTasks = taskClient.findRecentTasks(0, 100).getContents();
		List tasksWithoutTimelines = ["DE126", "US12406", "US12415", "US12418", "US12425", "DE1405", "US12392"]

		when:
		for (Task task : recentTasks) {
			try {
				timelineResource.getTimelineOverviewForTaskWithAllSubtasks(task.id)
			} catch (NotFoundException ex) {
				if (tasksWithoutTimelines.contains(task.name) == false) {
					throw ex
				}
			} catch (Exception ex) {
				throw new RuntimeException("Failed to retrieve timeline for taskName=${task.name}, taskId=${task.id}", ex)
			}
		}

		then:
		notThrown(Exception)

		when:
		faqClient.findAllFaq()

		then:
		notThrown(Exception)
	}

}

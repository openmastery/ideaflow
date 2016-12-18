package org.openmastery.publisher.core.stub

import org.openmastery.publisher.client.TaskClient
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.task.TaskService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FixtureDataGenerator {

	@Autowired
	private IdeaFlowPersistenceService persistenceService;

	@Autowired
	private TaskService taskService;

	@Value('http://localhost:${server.port}')
	private String hostUri;


	void generateStubTasks(Long userId, String apiKey) {
		if (taskExists(userId, "US12345")) {
			return
		}

		TaskClient taskClient = new TaskClient(hostUri).apiKey(apiKey)
		taskClient.createTask("US12345", "Create a new timeline chart", "dashboard")
		taskClient.createTask("DE1362", "Barchart is misaligned on screen", "dashboard")
		taskClient.createTask("US12364", "Configure the dashboard layout ", "dashboard")
		taskClient.createTask("US12378", "Create a slideout drawer", "dashboard")
		taskClient.createTask("US12392", "Make the bar colors prettier", "dashboard")
		taskClient.createTask("DE1405", "Report detail throws exception when null data", "dashboard")
		taskClient.createTask("US12406", "Send email notifications on update", "reporting")
		taskClient.createTask("US12415", "Generate annual PDF reports", "reporting")
		taskClient.createTask("US12418", "Update daily reporting jobs with timestamps", "reporting")
		taskClient.createTask("US12425", "Allow reports to be sent to multiple recipients", "reporting")
		taskClient.createTask("DE126", "Reporting job fails to start", "reporting")
	}


	private boolean taskExists(Long userId, String name) {
		persistenceService.findTaskWithName(userId, name)
	}



}

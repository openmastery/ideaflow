/*
 * Copyright 2017 New Iron Group, Inc.
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
package org.openmastery.publisher.core.stub

import com.bancvue.rest.exception.NotFoundException
import org.openmastery.publisher.api.batch.NewIFMBatch
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.BatchClient
import org.openmastery.publisher.client.TaskClient
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.event.EventEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FixtureDataGenerator {

	@Value('http://localhost:${server.port}')
	private String hostUri;

	@Autowired
	private IdeaFlowPersistenceService persistenceService

	private BatchLoader batchLoader = new BatchLoader()

	TaskClient taskClient
	BatchClient batchClient

	void connect(String bearerToken) {
		taskClient = new TaskClient(hostUri).bearerToken(bearerToken)
		batchClient = new BatchClient(hostUri).bearerToken(bearerToken)
	}

	void generateStubTasks() {

		createTask("US00012", "*** 3-hour feature task with no interruptions", "dashboard")
		createTask("DE1362", "*** Sample defect pattern with front-loaded troubleshooting", "dashboard")
		createTask("US12364", "*** Multi-day task with idles ", "dashboard")
		createTask("US12378", "*** Subtask with detailed notes and snippets", "dashboard")

		createTask("US12392", "Make the bar colors prettier", "dashboard")
		createTask("DE1405", "Report detail throws exception when null data", "dashboard")
		createTask("US12406", "Send email notifications on update", "reporting")
		createTask("US12415", "Generate annual PDF reports", "reporting")
		createTask("US12418", "Update daily reporting jobs with timestamps", "reporting")
		createTask("US12425", "Allow reports to be sent to multiple recipients", "reporting")
		createTask("DE126", "Reporting job fails to start", "reporting")

	}

	private createTask(String taskName, String description, String project) {
		try {
			Task task = taskClient.findTaskWithName(taskName)
			task.description = description
			task.project = project
			taskClient.update(task)

			if (noDataExists(task.id)) {
				loadBatch(task)
			}

		} catch (NotFoundException ex) {
			Task task = taskClient.createTask(taskName, description, project)
			loadBatch(task)
		}
	}

	void loadBatch(Task task) {
		NewIFMBatch batch = batchLoader.loadAndAdjust(task.id, task.creationDate, "/stub/task_"+task.name+".batch")
		if (batch && !batch.isEmpty()) {
			batchClient.addIFMBatch(batch)
		}
	}

	boolean noDataExists(Long taskId) {
		List<EventEntity> events = persistenceService.getEventList(taskId)
		return events.isEmpty()
	}


}

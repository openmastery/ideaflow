package org.openmastery.publisher.core

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.core.activity.ActivityRepository
import org.openmastery.publisher.core.event.EventRepository
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateRepository
import org.openmastery.publisher.core.task.TaskRepository
import org.springframework.beans.factory.annotation.Autowired

@ComponentTest
class IdeaFlowRelationalPersistenceServiceSpec extends IdeaFlowPersistenceServiceSpec {

	private IdeaFlowRelationalPersistenceService persistenceService
	@Autowired
	private IdeaFlowStateRepository ideaFlowStateRepository
	@Autowired
	private ActivityRepository activityRepository
	@Autowired
	private EventRepository eventRepository
	@Autowired
	private TaskRepository taskRepository

	def setup() {
		persistenceService = new IdeaFlowRelationalPersistenceService()
		persistenceService.ideaFlowStateRepository = ideaFlowStateRepository
		persistenceService.activityRepository = activityRepository
		persistenceService.eventRepository = eventRepository
		persistenceService.taskRepository = taskRepository
	}

	@Override
	protected IdeaFlowPersistenceService getPersistenceService() {
		persistenceService
	}

}

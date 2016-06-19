package org.openmastery.publisher.core

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.core.activity.EditorActivityRepository
import org.openmastery.publisher.core.activity.IdleActivityRepository
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
	private IdleActivityRepository idleActivityRepository
	@Autowired
	private EventRepository eventRepository
	@Autowired
	private EditorActivityRepository editorActivityRepository
	@Autowired
	private TaskRepository taskRepository

	def setup() {
		persistenceService = new IdeaFlowRelationalPersistenceService()
		persistenceService.ideaFlowStateRepository = ideaFlowStateRepository
		persistenceService.idleActivityRepository = idleActivityRepository
		persistenceService.eventRepository = eventRepository
		persistenceService.editorActivityRepository = editorActivityRepository
		persistenceService.taskRepository = taskRepository
	}

	@Override
	protected IdeaFlowPersistenceService getPersistenceService() {
		persistenceService
	}

}

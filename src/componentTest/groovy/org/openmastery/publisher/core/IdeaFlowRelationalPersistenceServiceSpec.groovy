package org.openmastery.publisher.core

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.core.activity.ActivityRepository
import org.openmastery.publisher.core.event.EventRepository
import org.openmastery.publisher.core.ideaflow.IdeaFlowPartialStateRepository
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateRepository
import org.openmastery.publisher.core.task.TaskRepository
import org.springframework.beans.factory.annotation.Autowired

@ComponentTest
class IdeaFlowRelationalPersistenceServiceSpec extends IdeaFlowPersistenceServiceSpec {

	@Autowired
	private IdeaFlowPersistenceService persistenceService

	@Override
	protected IdeaFlowPersistenceService getPersistenceService() {
		assert persistenceService.h.advised.targetSource.target instanceof IdeaFlowRelationalPersistenceService
		persistenceService
	}

}

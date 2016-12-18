package org.openmastery.publisher.core

import org.openmastery.publisher.ComponentTest
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

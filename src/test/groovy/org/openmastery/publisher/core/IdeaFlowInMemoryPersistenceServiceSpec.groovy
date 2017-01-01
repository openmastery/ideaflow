package org.openmastery.publisher.core

import spock.lang.Ignore

@Ignore
class IdeaFlowInMemoryPersistenceServiceSpec extends IdeaFlowPersistenceServiceSpec {

	IdeaFlowInMemoryPersistenceService persistenceService = new IdeaFlowInMemoryPersistenceService()

	@Override
	protected IdeaFlowPersistenceService getPersistenceService() {
		return persistenceService
	}

}

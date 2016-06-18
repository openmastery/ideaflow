package org.openmastery.publisher.core


class IdeaFlowInMemoryPersistenceServiceSpec extends IdeaFlowPersistenceServiceSpec {

	IdeaFlowInMemoryPersistenceService persistenceService = new IdeaFlowInMemoryPersistenceService()

	@Override
	protected IdeaFlowPersistenceService getPersistenceService() {
		return persistenceService
	}

}

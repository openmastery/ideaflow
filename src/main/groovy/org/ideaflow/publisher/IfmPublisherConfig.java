package org.ideaflow.publisher;

import org.ideaflow.publisher.core.ideaflow.IdeaFlowInMemoryPersistenceService;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IfmPublisherConfig {

	@Bean
	public IdeaFlowPersistenceService ideaFlowPersistenceService() {
		return new IdeaFlowInMemoryPersistenceService();
	}

}

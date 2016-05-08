package org.ideaflow.publisher;

import org.ideaflow.publisher.core.ideaflow.IdeaFlowInMemoryPersistenceService;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService;
import org.springframework.boot.actuate.autoconfigure.ManagementSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
		SecurityAutoConfiguration.class,
		ManagementSecurityAutoConfiguration.class})
public class IfmPublisherConfig {

	@Bean
	public IdeaFlowPersistenceService ideaFlowPersistenceService() {
		return new IdeaFlowInMemoryPersistenceService();
	}

}

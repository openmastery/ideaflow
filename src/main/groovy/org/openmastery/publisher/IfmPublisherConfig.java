package org.openmastery.publisher;

import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.openmastery.publisher.core.IdeaFlowRelationalPersistenceService;
import org.openmastery.time.LocalDateTimeService;
import org.openmastery.time.TimeService;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.openmastery.publisher")
@EnableAutoConfiguration(exclude = {
		SecurityAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class})
public class IfmPublisherConfig {

	@Bean
	public IdeaFlowPersistenceService ideaFlowPersistenceService() {
		return new IdeaFlowRelationalPersistenceService();
	}

	@Bean
	public TimeService timeService() {
		return new LocalDateTimeService();
	}

}

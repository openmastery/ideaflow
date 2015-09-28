package com.ideaflow.localagent.config;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {

	@PostConstruct
	public void initialize() {
		property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
		packages("com.ideaflow.localagent.resources");
	}

}


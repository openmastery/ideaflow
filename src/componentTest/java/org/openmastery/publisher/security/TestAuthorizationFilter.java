package org.openmastery.publisher.security;

import org.openmastery.publisher.core.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;

public class TestAuthorizationFilter extends AuthorizationFilter {

	@Autowired
	private UserEntity testUser;
	@Autowired
	private InvocationContext invocationContext;

	@Override
	public void filter(ContainerRequestContext request) {
		invocationContext.setUserId(testUser.getId());
	}

}

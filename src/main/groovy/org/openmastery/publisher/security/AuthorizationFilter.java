/**
 * Copyright 2017 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.security;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.impl.config.ResourcePropertiesSource;
import lombok.extern.slf4j.Slf4j;
import org.openmastery.publisher.api.ResourcePaths;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;

/**
 * NOTE: the use of Autowired to inject dependencies is intentional.  Funky, but intentional.  The AuthorizationFilter needs
 * to be a jersey-managed object so it can be prioritized alongside the other jersey filters.  However, the InvocationContext
 * needs to be spring-managed beans (so we can use the spring @Scope annotation rather than jersey's suck-ass request
 * scoping mechanism).
 * <p>
 * I think the correct fix for this is to convert all filters to be spring-managed rather than jersey-managed.
 */
@Slf4j
@Priority(Priorities.AUTHORIZATION)
@Provider
public class AuthorizationFilter implements ContainerRequestFilter, WriterInterceptor {

	private static final String BEARER_TOKEN_PATH = ResourcePaths.USER_PATH.substring(1) + ResourcePaths.BEARER_TOKEN_PATH;

	@Autowired
	private InvocationContext invocationContext;
	@Autowired
	private UserIdResolver userIdResolver;
	@Autowired
	private StormpathService stormpathService;

	@Override
	public void filter(ContainerRequestContext request) {
		if (HttpMethod.OPTIONS.equals(request.getRequest().getMethod())) {
			return;
		}
		// bearer token request requires the api key as input so authentication is performed in the resource
		// org.openmastery.publisher.resources.UserResource.getBearerToken
		if (BEARER_TOKEN_PATH.equals(request.getUriInfo().getPath())) {
			return;
		}

		String bearerToken = getBearerToken(request);
		if (bearerToken == null) {
			log.warn("Failed to resolve bearer token");
			throw new ForbiddenException("User not authenticated");
		}

		Account account = stormpathService.authenticate(bearerToken);
		invocationContext.setStormpathAccount(account);

		Long userId = userIdResolver.findOrCreateUserIdByEmail(account.getEmail());
		invocationContext.setUserId(userId);
	}

	private String getBearerToken(ContainerRequestContext request) {
		String authorizationHeader = request.getHeaderString("Authorization");
		if (authorizationHeader == null) {
			return null;
		}

		if (authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		} else {
			return null;
		}
	}

	@Override
	public void aroundWriteTo(final WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
		writerInterceptorContext.proceed();
	}

}

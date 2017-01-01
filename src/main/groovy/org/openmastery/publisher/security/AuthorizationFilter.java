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

import lombok.extern.slf4j.Slf4j;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.openmastery.publisher.core.user.UserEntity;
import org.openmastery.publisher.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Priority;
import javax.inject.Named;
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
@Named
public class AuthorizationFilter implements ContainerRequestFilter, WriterInterceptor {

	@Autowired
	private InvocationContext invocationContext;
	@Autowired
	private UserIdResolver userIdResolver;

	@Override
	public void filter(ContainerRequestContext request) {
		if (HttpMethod.OPTIONS.equals(request.getRequest().getMethod())) {
			return;
		}

		String apiKey = request.getHeaderString(ResourcePaths.API_KEY_HEADER);
		if (apiKey == null) {
			throw new ForbiddenException("Missing API key, header=" + ResourcePaths.API_KEY_HEADER);
		}

		Long userId = userIdResolver.findUserIdByApiKey(apiKey);
		if (userId == null) {
			throw new ForbiddenException("Failed to resolve user with apiKey=" + apiKey);
		}
		invocationContext.setUserId(userId);
	}

	@Override
	public void aroundWriteTo(final WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
		writerInterceptorContext.proceed();
	}

}

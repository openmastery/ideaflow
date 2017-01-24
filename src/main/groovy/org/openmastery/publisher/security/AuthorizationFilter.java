/**
 * Copyright 2017 New Iron Group, Inc.
 * <p>
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/gpl-3.0.en.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.security;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.provider.ProviderAccountResolver;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticationBuilder;
import com.stormpath.sdk.oauth.OAuthRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequests;
import com.stormpath.sdk.servlet.account.AccountResolver;
import lombok.extern.slf4j.Slf4j;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.openmastery.publisher.core.user.UserEntity;
import org.openmastery.publisher.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Priority;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
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
	@Autowired
	private HttpServletRequest servletRequest;
	@Autowired
	private AccountResolver accountResolver;
	@Autowired
	private Application stormpathApplication;

	@Override
	public void filter(ContainerRequestContext request) {
		if (HttpMethod.OPTIONS.equals(request.getRequest().getMethod())) {
			return;
		}

		String bearerToken = getBearerToken(request);
		if (bearerToken == null) {
			// TODO: log
			throw new ForbiddenException("User not authenticated");
		}

		OAuthBearerRequestAuthentication authRequest = OAuthRequests
				.OAUTH_BEARER_REQUEST
				.builder()
				.setJwt(bearerToken)
				.build();

		OAuthBearerRequestAuthenticationResult result = Authenticators
				.OAUTH_BEARER_REQUEST_AUTHENTICATOR
				.forApplication(stormpathApplication)
				.withLocalValidation()
				.authenticate(authRequest);

		Account account = result.getAccount();
		invocationContext.setStormpathAccount(account);

		Long userId = userIdResolver.findUserIdByEmail(account.getEmail());
		if (userId == null) {
			throw new ForbiddenException("Failed to resolve user with email=" + account.getEmail());
		}
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

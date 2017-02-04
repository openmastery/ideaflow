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
package org.openmastery.publisher.core.stub;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.account.EmailVerificationStatus;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequests;
import org.apache.commons.lang3.RandomStringUtils;
import org.openmastery.publisher.core.user.UserEntity;
import org.openmastery.publisher.core.user.UserRepository;
import org.openmastery.publisher.security.StormpathApiKey;
import org.openmastery.publisher.security.StormpathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Iterator;

@Component
@ConditionalOnMissingClass("org.openmastery.publisher.ComponentTest")
public class FixtureTimelineInitializer {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FixtureDataGenerator fixtureDataGenerator;
	@Autowired
	private StormpathService stormpathService;

	public void initialize() {
		initializeUserAccount("everything-is-awesome@openmastery.org");
		String demoUserBearerToken = initializeUserAccount("demo@openmastery.org");

		fixtureDataGenerator.connect(demoUserBearerToken);
		fixtureDataGenerator.generateStubTasks();
	}

	String initializeUserAccount(String userEmail) {
		Account account = stormpathService.findOrCreateStormpathAccountWithEmail(userEmail);
		UserEntity user = userRepository.findByEmail(userEmail);
		if (user == null) {
			user = UserEntity.builder()
					.email(userEmail)
					.build();
			userRepository.save(user);
		}

		StormpathApiKey apiKey = stormpathService.getOrCreateApiKey(account);
		String bearerToken = stormpathService.getBearerToken(apiKey);
		System.out.println("************************************************************************************************");
		System.out.println("************************************************************************************************");
		System.out.println("************************************************************************************************");
		System.out.println("Email        : " + account.getEmail());
		System.out.println("API Key      : " + apiKey.getEncodedValue());
		System.out.println("Bearer Token : " + bearerToken);
		System.out.println("Bearer Token Curl...");
		System.out.println(stormpathService.getBearerTokenCurl(apiKey));
		System.out.println("************************************************************************************************");
		System.out.println("************************************************************************************************");
		System.out.println("************************************************************************************************");

		return bearerToken;
	}

}

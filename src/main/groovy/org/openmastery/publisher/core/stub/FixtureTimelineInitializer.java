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
	private Client stormpathClient;
	@Autowired
	private Application stormpathApplication;
	@Autowired
	private FixtureDataGenerator fixtureDataGenerator;
	private String stormpathDnsLabel;

	@PostConstruct
	private void setStormpathDnsLabel() {
		stormpathDnsLabel = stormpathApplication.getWebConfig().getDnsLabel();
	}

	public void initialize() {
		initializeUserAccount("everything-is-awesome@openmastery.org");
		String demoUserBearerToken = initializeUserAccount("demo@openmastery.org");

		fixtureDataGenerator.connect(demoUserBearerToken);
		fixtureDataGenerator.generateStubTasks();
	}

	String initializeUserAccount(String userEmail) {
		Account account = findOrCreateStormpathAccountWithEmail(userEmail);
		UserEntity user = userRepository.findByEmail(userEmail);
		if (user == null) {
			user = UserEntity.builder()
					.email(userEmail)
					.build();
			userRepository.save(user);
		}

		ApiKey apiKey = getOrCreateApiKey(account);
		String bearerToken = getBearerToken(apiKey);
		System.out.println("************************************************************************************************");
		System.out.println("************************************************************************************************");
		System.out.println("************************************************************************************************");
		System.out.println("Email        : " + account.getEmail());
		System.out.println("API Key      : " + base64EncodeApiKey(apiKey));
		System.out.println("Bearer Token : " + bearerToken);
		System.out.println("Bearer Token Curl...");
		System.out.println(getBearerTokenCurl(apiKey));
		System.out.println("************************************************************************************************");
		System.out.println("************************************************************************************************");
		System.out.println("************************************************************************************************");

		return bearerToken;
	}

	private String getBearerTokenCurl(ApiKey apiKey) {
		String encodedApiKey = base64EncodeApiKey(apiKey);
		return "curl -X POST -H \"Accept: application/json\" -H \"Content-Type: application/x-www-form-urlencoded\" -H \"Authorization: Basic " + encodedApiKey + "\" --data \"grant_type=client_credentials\" https://" + stormpathDnsLabel + ".apps.stormpath.io/oauth/token";
	}

	private Account findOrCreateStormpathAccountWithEmail(String email) {
		AccountList accountList = stormpathApplication.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase(email)));
		if (accountList.getSize() == 0) {
			Account account = stormpathClient.instantiate(Account.class);
			account.setGivenName(email.substring(0, email.indexOf('@')));
			account.setEmail(email);
			account.setPassword(RandomStringUtils.randomAlphanumeric(10));
			account.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
			stormpathApplication.createAccount(account);
			return account;
		} else {
			return accountList.single();
		}
	}

	private ApiKey getOrCreateApiKey(Account account) {
		Iterator<ApiKey> apiKeyIterator = account.getApiKeys().iterator();
		if (apiKeyIterator.hasNext()) {
			return apiKeyIterator.next();
		} else {
			return account.createApiKey();
		}
	}

	private String base64EncodeApiKey(ApiKey apiKey) {
		String apiKeyString = apiKey.getId() + ":" + apiKey.getSecret();
		byte[] encodedBytes = Base64.getEncoder().encode(apiKeyString.getBytes());
		return new String(encodedBytes);
	}

	private String getBearerToken(ApiKey apiKey) {
		OAuthClientCredentialsGrantRequestAuthentication authRequest = OAuthRequests
				.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST
				.builder()
				.setApiKeyId(apiKey.getId())
				.setApiKeySecret(apiKey.getSecret())
				.build();

		OAuthGrantRequestAuthenticationResult result = Authenticators
				.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST_AUTHENTICATOR
				.forApplication(stormpathApplication)
				.authenticate(authRequest);
		return result.getAccessTokenString();
	}

}

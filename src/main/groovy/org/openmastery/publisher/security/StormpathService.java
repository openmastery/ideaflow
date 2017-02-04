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
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.account.EmailVerificationStatus;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequests;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;

@Component
public class StormpathService {

	@Autowired
	private Client stormpathClient;
	@Autowired
	private Application stormpathApplication;
	private String stormpathDnsLabel;

	@PostConstruct
	private void setStormpathDnsLabel() {
		stormpathDnsLabel = stormpathApplication.getWebConfig().getDnsLabel();
	}

	public String getBearerTokenCurl(StormpathApiKey apiKey) {
		return "curl -X POST " +
				"-H \"Accept: application/json\" " +
				"-H \"Content-Type: application/x-www-form-urlencoded\" " +
				"-H \"Authorization: Basic " + apiKey.getEncodedValue() + "\" " +
				"--data \"grant_type=client_credentials\" " +
				"https://" + stormpathDnsLabel + ".apps.stormpath.io/oauth/token";
	}

	public Account findOrCreateStormpathAccountWithEmail(String email) {
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

	public StormpathApiKey getOrCreateApiKey(Account account) {
		Iterator<ApiKey> apiKeyIterator = account.getApiKeys().iterator();
		ApiKey apiKey;
		if (apiKeyIterator.hasNext()) {
			apiKey = apiKeyIterator.next();
		} else {
			apiKey = account.createApiKey();
		}
		return new StormpathApiKey(apiKey);
	}

	public String getBearerToken(String encodedApiKey) {
		StormpathApiKey apiKey = StormpathApiKey.decode(encodedApiKey);
		return getBearerToken(apiKey);
	}

	public String getBearerToken(StormpathApiKey apiKey) {
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

	public Account authenticate(String bearerToken) {
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

		return result.getAccount();
	}

}

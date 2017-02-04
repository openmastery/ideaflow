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

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.impl.api.ClientApiKey;
import org.omg.CORBA.DynAnyPackage.Invalid;

import java.util.Base64;

public class StormpathApiKey {

	public static StormpathApiKey decode(String encodedApiKey) {
		byte[] decodedBytes = Base64.getDecoder().decode(encodedApiKey);
		String decodedApiKey = new String(decodedBytes);
		int separatorIndex = decodedApiKey.indexOf(':');
		if (separatorIndex < 0) {
			throw new InvalidApiKeyException(encodedApiKey);
		}
		String id = decodedApiKey.substring(0, separatorIndex);
		String secret = decodedApiKey.substring(separatorIndex + 1, decodedApiKey.length());
		ClientApiKey clientApiKey = new ClientApiKey(id, secret);
		return new StormpathApiKey(clientApiKey);
	}

	private ApiKey apiKey;

	public StormpathApiKey(ApiKey apiKey) {
		this.apiKey = apiKey;
	}

	public String getId() {
		return apiKey.getId();
	}

	public String getSecret() {
		return apiKey.getSecret();
	}

	public String getEncodedValue() {
		String apiKeyString = apiKey.getId() + ":" + apiKey.getSecret();
		byte[] encodedBytes = Base64.getEncoder().encode(apiKeyString.getBytes());
		return new String(encodedBytes);
	}

	private static class InvalidApiKeyException extends RuntimeException {
		public InvalidApiKeyException(String encodedApiKey) {
			super("Failed to decode ApiKey=" + encodedApiKey);
		}
	}

}

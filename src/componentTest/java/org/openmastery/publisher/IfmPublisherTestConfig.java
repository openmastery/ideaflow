/**
 * Copyright 2015 New Iron Group, Inc.
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
package org.openmastery.publisher;

import groovyx.net.http.RESTClient;
import org.openmastery.publisher.client.BatchClient;
import org.openmastery.publisher.client.TaskEventClient;
import org.openmastery.publisher.client.TimelineClient;
import org.openmastery.publisher.client.TaskClient;
import org.openmastery.publisher.core.user.UserEntity;
import org.openmastery.publisher.security.UserIdResolver;
import org.openmastery.storyweb.client.FaqClient;
import org.openmastery.storyweb.client.GlossaryClient;
import org.openmastery.storyweb.client.MetricsClient;
import org.openmastery.time.MockTimeService;
import org.openmastery.time.TimeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.net.URISyntaxException;
import java.util.UUID;

@Configuration
public class IfmPublisherTestConfig {

	@Value("${test-server.base_url:http://localhost}")
	private String serverBaseUrl;
	@Value("${test-server.base_url:http://localhost}:${server.port}")
	private String hostUri;

	@Bean
	public UserEntity testUser() {
		return UserEntity.builder()
				.id(42L)
				.apiKey(UUID.randomUUID().toString())
				.email("test-user@openmastery.org")
				.name("Joe Black")
				.build();
	}

	@Bean
	@Primary
	public UserIdResolver userIdResolver() {
		return new StubUserIdResolver(testUser());
	}

	@Bean
	public TimelineClient ideaFlowClient() {
		return new TimelineClient(hostUri)
				.apiKey(testUser().getApiKey());
	}

	@Bean
	public TaskEventClient eventClient() {
		return new TaskEventClient(hostUri)
				.apiKey(testUser().getApiKey());
	}

	@Bean
	public BatchClient activityClient() {
		return new BatchClient(hostUri)
				.apiKey(testUser().getApiKey());
	}

	@Bean
	public TaskClient taskClient() {
		return new TaskClient(hostUri)
				.apiKey(testUser().getApiKey());
	}

	@Bean
	public MetricsClient spcClient() {
		return new MetricsClient(hostUri)
				.apiKey(testUser().getApiKey());
	}

	@Bean
	public GlossaryClient glossaryClient() {
		return new GlossaryClient(hostUri)
				.apiKey(testUser().getApiKey());
	}

	@Bean
	public FaqClient faqClient() {
		return new FaqClient(hostUri)
				.apiKey(testUser().getApiKey());
	}

	@Bean
	@Primary
	public TimeService timeService() {
		return new MockTimeService();
	}

	@Bean
	@Primary
	public RESTClient restClient() throws URISyntaxException {
		RESTClient client = new RESTClient(hostUri);
		return client;
	}

	@Bean
	public RESTClient managementRestClient(@Value("${management.port}") String managementPort) throws URISyntaxException {
		RESTClient client = new RESTClient(serverBaseUrl + ":" + managementPort);
		return client;
	}


	private static class StubUserIdResolver implements UserIdResolver {

		private UserEntity user;

		public StubUserIdResolver(UserEntity user) {
			this.user = user;
		}

		@Override
		public Long findUserIdByApiKey(String apiKey) {
			return user.getApiKey().equals(apiKey) ? user.getId() : null;
		}

		@Override
		public Long findUserIdByEmail(String email) {
			return user.getEmail().equals(email) ? user.getId() : null;
		}
	}

}

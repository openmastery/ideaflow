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
package org.ideaflow.publisher;

import org.ideaflow.publisher.core.TimeService;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowInMemoryPersistenceService;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService;
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateMachine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
		SecurityAutoConfiguration.class,
		ManagementSecurityAutoConfiguration.class})
public class IfmPublisher {
	public static void main(String[] args) {
		SpringApplication.run(IfmPublisher.class, args);
	}

	@Bean
	public IdeaFlowPersistenceService ideaFlowPersistenceService() {
		IdeaFlowInMemoryPersistenceService persistenceService = new IdeaFlowInMemoryPersistenceService();
		MockTimeService timeService = new MockTimeService();
		IdeaFlowStateMachine stateMachine = new IdeaFlowStateMachine();
		stateMachine.timeService = timeService;
		stateMachine.ideaFlowPersistenceService = persistenceService;

		stateMachine.startTask();
		timeService.plusHour();
		stateMachine.startLearning("first learning");
		timeService.plusHours(3);
		stateMachine.stopLearning("stop first learning");


		return persistenceService;
	}


	private static class MockTimeService implements TimeService {

		private LocalDateTime now;

		MockTimeService() {
			now = LocalDateTime.of(2016, 1, 1, 0, 0);
		}

		@Override
		public LocalDateTime now() {
			return now;
		}

		public MockTimeService plusHour() {
			return plusHours(1);
		}

		public MockTimeService plusHours(int hours) {
			now = now.plusHours(hours);
			return this;
		}

		public MockTimeService plusMinute() {
			return plusMinutes(1);
		}

		public MockTimeService plusMinutes(int minutes) {
			now = now.plusMinutes(minutes);
			return this;
		}

	}

}

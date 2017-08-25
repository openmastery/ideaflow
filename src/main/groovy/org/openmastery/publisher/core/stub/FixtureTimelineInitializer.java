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

import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.openmastery.publisher.core.user.UserEntity;
import org.openmastery.publisher.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
@ConditionalOnMissingClass("org.openmastery.publisher.ComponentTest")
public class FixtureTimelineInitializer {

	@Autowired
	private IdeaFlowPersistenceService persistenceService;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	FixtureDataGenerator fixtureDataGenerator;

	public void initialize() {

		UserEntity masterUser = initializeUser("everything-is-awesome@openmastery.org");
		UserEntity demoUser = initializeUser("demo@openmastery.org");

		fixtureDataGenerator.connect(demoUser.getApiKey());
		fixtureDataGenerator.generateStubTasks();

	}

	UserEntity initializeUser(String email) {
		String userEmail = email;
		UserEntity user = userRepository.findByEmail(userEmail);
		if (user == null) {
			user = UserEntity.builder()
					.email(userEmail)
					.apiKey(UUID.randomUUID().toString())
					.name("@torchie")
					.build();
			userRepository.save(user);
		}

		System.out.println("************************************************************************************************");
		System.out.println("************************************************************************************************");
		System.out.println("************************************************************************************************");
		System.out.println("Email   : " + user.getEmail());
		System.out.println("API Key : " + user.getApiKey());
		System.out.println("************************************************************************************************");
		System.out.println("************************************************************************************************");
		System.out.println("************************************************************************************************");

		return user;
	}

}

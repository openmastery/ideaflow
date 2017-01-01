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
package org.openmastery;

import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.openmastery.publisher.core.IdeaFlowRelationalPersistenceService;
import org.openmastery.time.LocalDateTimeService;
import org.openmastery.time.TimeService;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"org.openmastery.publisher", "org.openmastery.storyweb"})
@EnableAutoConfiguration(exclude = {
		SecurityAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class})
public class IfmPublisherConfig {

	@Bean
	public IdeaFlowPersistenceService ideaFlowPersistenceService() {
		return new IdeaFlowRelationalPersistenceService();
	}

	@Bean
	public TimeService timeService() {
		return new LocalDateTimeService();
	}

}

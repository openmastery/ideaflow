/**
 * Copyright 2016 New Iron Group, Inc.
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
package org.openmastery.publisher.config;

import com.bancvue.rest.config.ObjectMapperContextResolver;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.openmastery.logging.LoggingFilter;
import org.openmastery.publisher.security.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {

	@Autowired
	protected AuthorizationFilter authorizationFilter;

	@PostConstruct
	public void initialize() {
		property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
		packages("org.openmastery.publisher.resources");
		packages("org.openmastery.storyweb.resources");
		register(LoggingFilter.class);
		register(CORSResponseFilter.class);
		//register(ObjectMapperContextResolver.class);
		register(CustomValueTypeResolver.class);
		register(authorizationFilter);
	}

}


/**
 * Copyright 2015 New Iron Group, Inc.
 * <p/>
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.gnu.org/licenses/gpl-3.0.en.html
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ideaflow.publisher.config;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {

	@PostConstruct
	public void initialize() {
		property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
		packages("org.ideaflow.publisher.resources");
	}

}


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
package org.openmastery.publisher.resources;


import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.core.user.UserEntity;
import org.openmastery.publisher.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Component
@Path(ResourcePaths.USER_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	@Autowired
	private UserRepository userRepository;

	@POST
	public String createUser(@QueryParam("email") String userEmail) {

		UserEntity user = UserEntity.builder()
			.email(userEmail)
			.apiKey(UUID.randomUUID().toString())
			.build();
		userRepository.save(user);

		return user.getApiKey() + "\n";
	}
}

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
package org.openmastery.publisher.resources;


import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.core.user.UserEntity;
import org.openmastery.publisher.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Component
@Path(ResourcePaths.USER_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Create a new user API-Key based on the specified email.
	 * @exclude
	 * @param userEmail a unique email account
	 * @return API-Key
	 */
	@POST
	public String createUser(@QueryParam("email") String userEmail) {

		UserEntity user = UserEntity.builder()
			.email(userEmail)
			.apiKey(UUID.randomUUID().toString())
			.build();
		userRepository.save(user);

		return user.getApiKey() + "\n";
	}




	/**
	 * Retrieve the API-Key of an existing user
	 * @exclude
	 * @param userEmail a unique email account
	 * @return API-Key
	 */

	@GET
	@Path(ResourcePaths.APIKEY_PATH)
	public String getAPIKey(@QueryParam("email") String userEmail) {
		String apiKey = null;
		UserEntity user = userRepository.findByEmail(userEmail);
		if (user != null) {
			apiKey = user.getApiKey() + "\n";
		}
		return apiKey;
	}
}

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


import org.openmastery.mapper.ValueObjectMapper;
import org.openmastery.publisher.api.PagedResult;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.task.NewTask;
import org.openmastery.publisher.api.task.Task;
import org.openmastery.publisher.api.user.NewUser;
import org.openmastery.publisher.api.user.User;
import org.openmastery.publisher.core.event.EventEntity;
import org.openmastery.publisher.core.user.UserEntity;
import org.openmastery.publisher.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Path(ResourcePaths.USER_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	@Autowired
	private UserRepository userRepository;

	private ValueObjectMapper mapper = new ValueObjectMapper();

	/**
	 * Create a new user API-Key based on the specified email.
	 * @exclude
	 * @param userAccount a user's unique email account and name
	 * @return API-Key
	 */
	@POST
	public String createUser(NewUser userAccount) {
		UserEntity user = UserEntity.builder()
				.email(userAccount.getEmail())
				.name(userAccount.getName())
				.apiKey(UUID.randomUUID().toString())
				.build();
		userRepository.save(user);

		return user.getApiKey() + "\n";
	}

	/**
	 * Deletes a user based on their email.
	 * @param email identifies the user
	 */
	@DELETE
	public String deleteUser(String email) {
		UserEntity userToBeDeleted = userRepository.findByEmail(email);
		userRepository.delete(userToBeDeleted);
		return userToBeDeleted.getApiKey() + "\n";
	}

	/**
	 * Retrieves all available users and API-keys
	 * @exclude
	 * @return List<User>
	 */
	@GET
	public List<User> findAllUsers() {

		List<User> userList = new ArrayList<>();
		Iterable<UserEntity> userEntities = userRepository.findAll();
		for (UserEntity entity : userEntities) {
			if (entity.getName() == null || !entity.getName().equals("@torchie")) {
				userList.add( toApi(entity) );
			}
		}

		return userList;
	}


	private User toApi(UserEntity entity) {
		return mapper.mapIfNotNull(entity, User.class);
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

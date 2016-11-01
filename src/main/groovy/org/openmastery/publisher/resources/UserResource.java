package org.openmastery.publisher.resources;


import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.activity.NewActivityBatch;
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

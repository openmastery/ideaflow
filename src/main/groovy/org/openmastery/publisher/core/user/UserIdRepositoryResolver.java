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
package org.openmastery.publisher.core.user;

import org.openmastery.publisher.security.UserIdResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserIdRepositoryResolver implements UserIdResolver {

	@Autowired
	private UserRepository userRepository;

	@Override
	public Long findUserIdByApiKey(String apiKey) {
		UserEntity user = userRepository.findByApiKey(apiKey);
		return user == null ? null : user.getId();
	}

	@Override
	public Long findUserIdByEmail(String email) {
		UserEntity user = userRepository.findByEmail(email);
		return user == null ? null : user.getId();
	}

}

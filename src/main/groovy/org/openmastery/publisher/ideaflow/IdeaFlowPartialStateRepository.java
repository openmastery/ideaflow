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
package org.openmastery.publisher.ideaflow;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IdeaFlowPartialStateRepository extends PagingAndSortingRepository<IdeaFlowPartialStateEntity, IdeaFlowPartialStateEntity.PrimaryKey> {

	@Modifying
	@Query(nativeQuery = true, value = "delete from idea_flow_partial_state where task_id = ?1 and scope = ?2")
	int deleteIfExists(Long taskId, String scope);

}

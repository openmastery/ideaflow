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
package org.openmastery.publisher.core.event;

import org.openmastery.publisher.api.batch.NewBatchEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends PagingAndSortingRepository<EventEntity, Long> {

	List<EventEntity> findByTaskId(long taskId);


	@Query(nativeQuery = true, value = "select * from event where owner_id=:ownerId and position >= :position order by position asc limit :limit")
	List<EventEntity> findRecentEvents(@Param("ownerId") Long userId, @Param("position") LocalDateTime afterDate,  @Param("limit") Integer limit);
}

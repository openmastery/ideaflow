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
package org.openmastery.publisher.core.event;

import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.core.task.TaskEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface EventRepository extends PagingAndSortingRepository<EventEntity, Long> {

	List<EventEntity> findByTaskId(long taskId);


	@Query(nativeQuery = true, value = "select * from event where owner_id=:ownerId and position >= :position order by position asc limit :limit")
	List<EventEntity> findRecentEvents(@Param("ownerId") Long userId, @Param("position") Timestamp afterDate,  @Param("limit") Integer limit);


	@Query(nativeQuery = true, value = "select * from event where owner_id=(?1) and position between (?2) and (?3) order by position asc")
	List<EventEntity> findEventsWithinsRange(Long userId, Timestamp startTime, Timestamp endTime);


	@Query(nativeQuery = true, value = "select * from event where owner_id=(?1) order by position asc")
	List<EventEntity> findAllByUser(Long userId);

	EventEntity findByOwnerIdAndId(Long ownerId, Long eventId);


}

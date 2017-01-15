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
package org.openmastery.publisher.core.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface TaskRepository extends PagingAndSortingRepository<TaskEntity, Long> {

	TaskEntity findByOwnerIdAndName(Long ownerId, String name);

	@Query(nativeQuery = true, value = "select * from task where owner_id=:ownerId order by modify_date desc limit :limit")
	List<TaskEntity> findRecent(@Param("ownerId") Long userId, @Param("limit") int limit);

	Page<TaskEntity> findByOwnerIdAndProjectLike(@Param("ownerId") Long userId, @Param("project") String project, Pageable pageable);


	@Query(nativeQuery = true, value = "select count(*) from task t where t.owner_id=:ownerId and t.project like :project and " +
			"(exists (select 1 from event e where e.task_id=t.id and lower(e.comment) similar to (:pattern)) " +
			"or exists (select 1 from annotation faq " +
			"where faq.task_id=t.id and faq.type = 'faq' and lower(faq.metadata) similar to (:pattern))) ")
	Integer countTasksMatchingTags(@Param("ownerId") Long userId, @Param("project") String project, @Param("pattern") String tagPattern);

	@Query(nativeQuery = true, value = "select t.* from task t where t.owner_id=:ownerId and t.project like :project and " +
			"(exists " +
			"(select 1 from event e where e.task_id=t.id and lower(e.comment) similar to (:pattern)) " +
			"or exists " +
			"(select 1 from annotation faq where faq.task_id=t.id and faq.type = 'faq' and lower(faq.metadata) similar to (:pattern)) " +
			") order by t.modify_date desc, t.id limit :limit offset :offset")
	List<TaskEntity> findByOwnerIdAndMatchingTags(@Param("ownerId") Long userId, @Param("project") String project, @Param("pattern") String tagPattern,
												  @Param("limit") int limit,  @Param("offset") int offset);


	@Query(nativeQuery = true, value = "select * from task where owner_id=(?1) " +
			"and creation_date <= (?3) and modify_date >= (?2) order by modify_date desc")
	List<TaskEntity> findTasksWithinRange(Long userId, Timestamp startTime, Timestamp endTime);

	@Query(nativeQuery = true, value = "select * from task where owner_id=:ownerId and id in (:taskIds)")
	List<TaskEntity> findTasksWithIds(@Param("ownerId")Long userId, @Param("taskIds") List<Long> ids);

}

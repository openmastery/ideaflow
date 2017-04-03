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
package org.openmastery.publisher.core.annotation;

import org.openmastery.publisher.api.annotation.FAQAnnotation;
import org.openmastery.publisher.core.event.EventEntity;
import org.openmastery.storyweb.api.FaqSummary;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AnnotationRespository extends PagingAndSortingRepository<AnnotationEntity, Long> {

	@Query(nativeQuery = true, value = "select * from annotation where type = 'faq' and task_id = ?1")
	List<FaqAnnotationEntity> findFaqAnnotationsByTaskId(long taskId);

	@Query(nativeQuery = true, value = "select * from annotation where type = 'snippet' and task_id = ?1")
	List<SnippetAnnotationEntity> findSnippetsByTaskId(long taskId);

	@Query(nativeQuery = true, value = "select faq.task_id, faq.event_id, e.comment eventComment, faq.metadata faqJson, e.position " +
			"from event e left outer join annotation faq on e.id = faq.event_id where (faq.type = 'faq' or faq.type is null) " +
			"and (lower(faq.metadata) similar to (?1) or lower(e.comment) similar to (?1))")

	List<Object []> findFaqsBySearchCriteria(String pattern);

	@Query(nativeQuery = true, value = "select a.* from annotation a, event e where a.type = 'faq' " +
			"and a.event_id=e.id " +
			"and a.owner_id=(?1) " +
			"and position between (?2) and (?3) " +
			"order by position asc")
	List<FaqAnnotationEntity> findFaqsWithinRange(Long userId, Timestamp startTime, Timestamp endTime);

	@Query(nativeQuery = true, value = "select a.* from annotation a, event e where a.type = 'faq' " +
			"and a.event_id=e.id " +
			"and a.owner_id=(?1) " +
			"order by position asc")
	List<FaqAnnotationEntity> findAllFaqsByUser(Long userId);

	@Query(nativeQuery = true, value = "select a.* from annotation a where a.type = 'faq' " +
			"and a.task_id=(:taskId) " +
			"and a.owner_id=(:ownerId) ")
	List<FaqAnnotationEntity> findByOwnerIdAndTaskId(@Param("ownerId") Long ownerId, @Param("taskId") Long taskId);

	@Modifying
	@Transactional
	@Query(value="delete from annotation where event_id = ?1 and type = ?2")
	void deleteByEventAndType(long eventId, String type);

}

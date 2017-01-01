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
package org.openmastery.publisher.core.annotation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AnnotationRespository extends PagingAndSortingRepository<AnnotationEntity, Long> {

	@Query(nativeQuery = true, value = "select * from annotation where type = 'faq' and task_id = ?1")
	List<FaqAnnotationEntity> findFaqAnnotationsByTaskId(long taskId);

	@Query(nativeQuery = true, value = "select * from annotation where type = 'snippet' and task_id = ?1")
	List<SnippetAnnotationEntity> findSnippetsByTaskId(long taskId);

	@Modifying
	@Transactional
	@Query(value="delete from annotation where event_id = ?1 and type = ?2")
	void deleteByEventAndType(long eventId, String faq);
}

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
package org.openmastery.publisher.core.activity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ActivityRepository extends PagingAndSortingRepository<ActivityEntity, Long> {

	List<ActivityEntity> findByTaskId(long taskId);

	@Query(nativeQuery = true, value = "select * from activity where task_id = ?1 order by end_time desc limit 1")
	ActivityEntity findMostRecentActivityForTask(long taskId);

	@Query(nativeQuery = true, value = "select * from activity where type = 'idle' and task_id = ?1")
	List<IdleActivityEntity> findIdleActivityByTaskId(long taskId);

	@Query(nativeQuery = true, value = "select * from activity where type = 'external' and task_id = ?1")
	List<ExternalActivityEntity> findExternalActivityByTaskId(long taskId);

	@Query(nativeQuery = true, value = "select * from activity where type = 'editor' and task_id = ?1")
	List<EditorActivityEntity> findEditorActivityByTaskId(long taskId);

	@Query(nativeQuery = true, value = "select * from activity where type = 'modification' and task_id = ?1")
	List<ModificationActivityEntity> findModificationActivityByTaskId(long taskId);

	@Query(nativeQuery = true, value = "select * from activity where type = 'execution' and task_id = ?1")
	List<ExecutionActivityEntity> findExecutionActivityByTaskId(long taskId);

	@Query(nativeQuery = true, value = "select * from activity where type = 'block' and task_id = ?1")
	List<BlockActivityEntity> findBlockActivityByTaskId(long taskId);
}

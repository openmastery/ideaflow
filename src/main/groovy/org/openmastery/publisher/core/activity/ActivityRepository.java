package org.openmastery.publisher.core.activity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ActivityRepository extends PagingAndSortingRepository<ActivityEntity, Long> {

	@Query(nativeQuery = true, value = "select * from activity where task_id = ?1 order by end_time desc limit 1")
	ActivityEntity findMostRecentActivityForTask(long taskId);


	@Query(nativeQuery = true, value = "select * from activity where type = 'idle' and task_id = ?1")
	List<IdleActivityEntity> findIdleActivityByTaskId(long taskId);


	@Query(nativeQuery = true, value = "select * from activity where type = 'editor' and task_id = ?1")
	List<EditorActivityEntity> findEditorActivityByTaskId(long taskId);

}

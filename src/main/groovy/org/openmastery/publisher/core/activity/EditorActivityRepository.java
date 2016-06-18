package org.openmastery.publisher.core.activity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface EditorActivityRepository extends PagingAndSortingRepository<EditorActivityEntity, Long> {

	List<EditorActivityEntity> findByTaskId(long taskId);

	@Query(nativeQuery = true, value = "select * from editor_activity where task_id = ?1 order by end_time desc limit 1")
	EditorActivityEntity findMostRecentEditorActivityForTask(long taskId);

}

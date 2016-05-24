package org.ideaflow.publisher.core.activity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface EditorActivityRepository extends PagingAndSortingRepository<EditorActivityEntity, Long> {

	List<EditorActivityEntity> findByTaskId(long taskId);

//	@Query("select afrom editor_activity a where a.task_id = ?1 order by ")
//	EditorActivityEntity findMostRecentEditorActivityForTask(long taskId);

}

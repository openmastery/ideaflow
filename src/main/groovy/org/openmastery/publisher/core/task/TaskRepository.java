package org.openmastery.publisher.core.task;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TaskRepository extends PagingAndSortingRepository<TaskEntity, Long> {

	TaskEntity findByName(String name);

	@Query(nativeQuery = true, value = "select * from task order by creation_date desc limit ?1")
	List<TaskEntity> findRecent(int limit);

}

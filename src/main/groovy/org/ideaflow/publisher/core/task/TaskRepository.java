package org.ideaflow.publisher.core.task;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface TaskRepository extends PagingAndSortingRepository<TaskEntity, Long> {

	TaskEntity findByName(String name);

}

package org.openmastery.publisher.core.activity;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface IdleActivityRepository extends PagingAndSortingRepository<IdleActivityEntity, Long> {

	List<IdleActivityEntity> findByTaskId(long taskId);

}

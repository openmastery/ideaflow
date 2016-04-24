package org.ideaflow.publisher.core.task;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

	long id;
	long taskId;

	String taskName;
	String projectName;
	String author;
}

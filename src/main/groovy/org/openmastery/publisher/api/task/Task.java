package org.openmastery.publisher.api.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

	private Long id;
	private LocalDateTime creationDate;

	private String name;
	private String description;

}

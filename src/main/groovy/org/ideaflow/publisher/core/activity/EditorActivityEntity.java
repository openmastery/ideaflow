package org.ideaflow.publisher.core.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditorActivityEntity {

	private Long id;
	private Long taskId;

	private LocalDateTime start;
	private LocalDateTime end;

	private String filePath;
	private boolean isModified;

	public String getFileName() {
		return new File(filePath).getName();
	}

}

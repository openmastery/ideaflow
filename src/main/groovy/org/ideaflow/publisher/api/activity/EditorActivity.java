package org.ideaflow.publisher.api.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditorActivity {

	private Long taskId;
	private String filePath;
	private boolean isModified;
	private Duration duration;

	public String getFileName() {
		return new File(filePath).getName();
	}

}

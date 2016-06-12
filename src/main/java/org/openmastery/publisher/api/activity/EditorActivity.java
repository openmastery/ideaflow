package org.openmastery.publisher.api.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditorActivity {

	private Long taskId;
	private String filePath;
	private boolean isModified;
	private Long duration;

	public String getFileName() {
		return new File(filePath).getName();
	}

}

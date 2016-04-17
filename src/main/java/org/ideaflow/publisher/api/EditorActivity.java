package org.ideaflow.publisher.api;

import java.io.File;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditorActivity {

	private String filePath;
	private boolean isModified;
	// Did the developer decide to "zoom in" from the previous file?
	private String navigateFromLink;
	private Duration duration;

	public String getFileName() {
		return new File(filePath).getName();
	}

}

package org.openmastery.publisher.api.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEditorActivity {

	private Long taskId;
	private String filePath;
	private boolean isModified;
	private Long durationInSeconds;

}

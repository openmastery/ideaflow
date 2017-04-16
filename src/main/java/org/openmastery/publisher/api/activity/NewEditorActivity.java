package org.openmastery.publisher.api.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEditorActivity implements NewActivity {

	private Long taskId;
	private String filePath;
	private boolean isModified;
	private Long durationInSeconds;

	private LocalDateTime endTime;
}

package org.openmastery.publisher.api.activity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	private Long durationInSeconds;
	private Long relativePositionInSeconds;

	public String getFileName() {
		return new File(filePath).getName();
	}


	// simplify dozer mapping

	@JsonIgnore
	public Long getDuration() {
		return durationInSeconds;
	}

	@JsonIgnore
	public void setDuration(Long duration) {
		durationInSeconds = duration;
	}

}

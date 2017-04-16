package org.openmastery.publisher.api.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.activity.EditorActivity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySummary {

	private String activityType;
	private String activityName;
	private String activityDetail;

	private Long durationInSeconds;
	private Long durationModifiedInSeconds;

//	public ActivitySummary(EditorActivity editorActivity) {
//		this.durationInSeconds = editorActivity.getDurationInSeconds();
//		this.filePath = editorActivity.getFilePath();
//		this.isModified = editorActivity.isModified();
//	}
//
//
//	public void aggregateWith(EditorActivity editorActivity) {
//		durationInSeconds += editorActivity.getDurationInSeconds();
//		isModified |= editorActivity.isModified();
//	}
}

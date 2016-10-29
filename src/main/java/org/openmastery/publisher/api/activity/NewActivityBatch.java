package org.openmastery.publisher.api.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewActivityBatch {

	private LocalDateTime timeSent;
	private List<NewEditorActivity> editorActivityList;
	private List<NewExternalActivity> externalActivityList;
	private List<NewIdleActivity> idleActivityList;

	boolean isEmpty() {
		return editorActivityList.isEmpty() && externalActivityList.isEmpty() && idleActivityList.isEmpty();
	}

}

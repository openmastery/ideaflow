package org.openmastery.publisher.api.batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.activity.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewIFMBatch {

	private LocalDateTime timeSent;
	private List<NewEditorActivity> editorActivityList;
	private List<NewExternalActivity> externalActivityList;
	private List<NewIdleActivity> idleActivityList;
	private List<NewExecutionActivity> executionActivityList;
	private List<NewModificationActivity> modificationActivityList;

	private List<NewBatchEvent> eventList;

	public boolean isEmpty() {
		return editorActivityList.isEmpty() && externalActivityList.isEmpty() && idleActivityList.isEmpty()
				&& executionActivityList.isEmpty() && modificationActivityList.isEmpty() && eventList.isEmpty();
	}

}

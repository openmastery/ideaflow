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
	private List<NewBlockActivity> blockActivityList;

	private List<NewBatchEvent> eventList;

	public boolean isEmpty() {
		boolean hasContent = editorActivityList != null && !editorActivityList.isEmpty();
		hasContent |= externalActivityList != null && !externalActivityList.isEmpty();
		hasContent |= idleActivityList != null && !idleActivityList.isEmpty();
		hasContent |= executionActivityList != null && !executionActivityList.isEmpty();
		hasContent |= modificationActivityList != null && !modificationActivityList.isEmpty();
		hasContent |= blockActivityList != null && !blockActivityList.isEmpty();
		hasContent |= eventList != null && !eventList.isEmpty();


		return !hasContent;
	}

}

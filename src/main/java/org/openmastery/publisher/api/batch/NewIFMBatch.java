package org.openmastery.publisher.api.batch;

import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.activity.*;
import org.openmastery.publisher.api.event.NewSnippetEvent;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewIFMBatch {

	private LocalDateTime timeSent;

	@Singular("editorActivity") private List<NewEditorActivity> editorActivityList;
	@Singular("externalActivity") private List<NewExternalActivity> externalActivityList;
	@Singular("idleActivity") private List<NewIdleActivity> idleActivityList;
	@Singular("executionActivity") private List<NewExecutionActivity> executionActivityList;
	@Singular("modificationActivity") private List<NewModificationActivity> modificationActivityList;
	@Singular("blockActivity") private List<NewBlockActivity> blockActivityList;
	@Singular("event")private List<NewBatchEvent> eventList;
	@Singular("snippetEvent") private List<NewSnippetEvent> snippetEventList;

	public boolean isEmpty() {
		boolean hasContent = editorActivityList != null && !editorActivityList.isEmpty();
		hasContent |= externalActivityList != null && !externalActivityList.isEmpty();
		hasContent |= idleActivityList != null && !idleActivityList.isEmpty();
		hasContent |= executionActivityList != null && !executionActivityList.isEmpty();
		hasContent |= modificationActivityList != null && !modificationActivityList.isEmpty();
		hasContent |= blockActivityList != null && !blockActivityList.isEmpty();
		hasContent |= eventList != null && !eventList.isEmpty();
		hasContent |= snippetEventList != null && !snippetEventList.isEmpty();

		return !hasContent;
	}

}

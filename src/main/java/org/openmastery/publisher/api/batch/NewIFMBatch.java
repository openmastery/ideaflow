package org.openmastery.publisher.api.batch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.activity.*;
import org.openmastery.publisher.api.event.NewSnippetEvent;

import java.util.ArrayList;
import java.util.Collections;
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

	private List<List<? extends BatchItem>> getBatchItemLists() {
		ArrayList<List<? extends BatchItem>> batchItemLists = new ArrayList<List<? extends BatchItem>>(10);
		batchItemLists.add(getNotNullList(editorActivityList));
		batchItemLists.add(getNotNullList(externalActivityList));
		batchItemLists.add(getNotNullList(idleActivityList));
		batchItemLists.add(getNotNullList(executionActivityList));
		batchItemLists.add(getNotNullList(modificationActivityList));
		batchItemLists.add(getNotNullList(blockActivityList));
		batchItemLists.add(getNotNullList(eventList));
		batchItemLists.add(getNotNullList(snippetEventList));
		return batchItemLists;
	}

	private List<? extends BatchItem> getNotNullList(List<? extends BatchItem> list) {
		return list == null ? Collections.EMPTY_LIST : list;
	}

	@JsonIgnore
	public List<BatchItem> getBatchItems() {
		List<BatchItem> batchItems = new ArrayList<BatchItem>();
		for (List<? extends BatchItem> list : getBatchItemLists()) {
			batchItems.addAll(list);
		}
		return batchItems;
	}

	@JsonIgnore
	public boolean isEmpty() {
		for (List<? extends BatchItem> list : getBatchItemLists()) {
			if (list.isEmpty() == false) {
				return false;
			}
		}
		return true;
	}

}

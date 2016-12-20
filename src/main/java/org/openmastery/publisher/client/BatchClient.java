package org.openmastery.publisher.client;

import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.activity.*;
import org.openmastery.publisher.api.batch.NewBatchEvent;
import org.openmastery.publisher.api.batch.NewIFMBatch;
import org.openmastery.publisher.api.event.EventType;

import java.util.Arrays;

public class BatchClient extends OpenMasteryClient<EditorActivity, BatchClient> {

	public BatchClient(String baseUrl) {
		super(baseUrl, ResourcePaths.BATCH_PATH, EditorActivity.class);
	}

	public void addIFMBatch(NewIFMBatch batch) {
		crudClientRequest.createWithPost(batch);
	}

}

package org.openmastery.publisher.client;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.activity.EditorActivity;
import org.openmastery.publisher.api.batch.NewIFMBatch;

public class BatchClient extends OpenMasteryClient<EditorActivity, BatchClient> {

	public BatchClient(String baseUrl) {
		super(baseUrl, ResourcePaths.IDEAFLOW_PATH +
						ResourcePaths.COLLECT_PATH +
						ResourcePaths.BATCH_PATH, EditorActivity.class);
	}

	public void addIFMBatch(NewIFMBatch batch) {

		crudClientRequest.createWithPost(batch);
	}

}

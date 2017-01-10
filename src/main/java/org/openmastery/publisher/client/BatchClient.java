package org.openmastery.publisher.client;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.batch.NewIFMBatch;

public class BatchClient extends IdeaFlowClient<Object, BatchClient> {

	public BatchClient(String baseUrl) {
		super(baseUrl, ResourcePaths.IDEAFLOW_PATH +
						ResourcePaths.PUBLISHER_PATH +
						ResourcePaths.BATCH_PATH, Object.class);
	}

	public void addIFMBatch(NewIFMBatch batch) {

		crudClientRequest.createWithPost(batch);
	}

}

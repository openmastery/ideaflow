package org.openmastery.publisher.api.batch

import spock.lang.Specification


class NewIFMBatchTest extends Specification {

	def "getBatchItemLists should return all internal lists"() {
		given:
		NewIFMBatch batch = NewIFMBatch.builder().build()
		Map properties = batch.getProperties()
		properties.remove("timeSent")
		properties.remove("class")
		properties.remove("batchItemLists")
		properties.remove("batchItems")
		properties.remove("empty")

		expect:
		assert properties.size() == batch.getBatchItemLists().size() : "BatchItemList size mismatch - did you add a new " +
				"BatchItem list without adding it to getBatchItemLists?  If not, it's possible that a different variable " +
				"or accessor was added and the test needs to be updated to remove that property from the map"
	}

}

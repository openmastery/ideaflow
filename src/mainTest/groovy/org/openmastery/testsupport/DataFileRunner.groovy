package org.openmastery.testsupport

import org.joda.time.LocalDateTime
import org.openmastery.publisher.core.stub.BatchLoader

class DataFileRunner {

	public static void main(String [] args) {
		BatchLoader loader = new BatchLoader()
		LocalDateTime startTime = new LocalDateTime().minusDays(3)

		List<Object> activityObjects = loader.loadAndAdjustToConsecutiveTime('/stub/task_US12345.batch', -1L, startTime)

		File newFile = new File('/Users/janelle/code/ifm-publisher/src/main/resources/stub/task_US12345.out')

		if (newFile.exists()) newFile.delete()
		newFile.createNewFile()

		loader.writeRawBatchActivityList(newFile, activityObjects)
	}


}

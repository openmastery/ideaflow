package org.openmastery.testsupport

import org.joda.time.LocalDateTime
import org.openmastery.publisher.core.stub.BatchLoader

class DataFileRunner {

	public static void main(String [] args) {
		BatchLoader loader = new BatchLoader()
		LocalDateTime startTime = new LocalDateTime().minusDays(15)

		List<Object> activityObjects = loader.loadAndAdjustToConsecutiveTime('/stub/task_US12364.in', -1L, startTime)

		File newFile = new File('/Users/janelle/code/ifm-publisher/src/main/resources/stub/task_US12364.batch')

		if (newFile.exists()) newFile.delete()
		newFile.createNewFile()

		loader.writeRawBatchActivityList(newFile, activityObjects)

		println "Wrote "+newFile.getBytes().length+ "bytes to "+newFile.name

	}


}

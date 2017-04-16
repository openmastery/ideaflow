package org.openmastery.testsupport

import org.openmastery.publisher.core.stub.BatchLoader

import java.time.LocalDateTime

class DataFileRunner {

	public static void main(String [] args) {
		BatchLoader loader = new BatchLoader()
		LocalDateTime startTime = LocalDateTime.now().minusDays(15)

		List<Object> activityObjects = loader.loadAndAdjustToConsecutiveTime('/stub/task_US12378.in', -1L, startTime)

		File newFile = new File('/Users/janelle/code/openmastery/ideaflow/src/main/resources/stub/task_US12378.batch')

		if (newFile.exists()) newFile.delete()
		newFile.createNewFile()

		loader.writeRawBatchActivityList(newFile, activityObjects)

		println "Wrote "+newFile.getBytes().length+ "bytes to "+newFile.name

	}


}

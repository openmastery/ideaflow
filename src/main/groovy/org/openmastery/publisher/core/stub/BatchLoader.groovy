package org.openmastery.publisher.core.stub

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewBlockActivity
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewIdleActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.api.batch.NewIFMBatch


class BatchLoader {

	private JSONConverter jsonConverter = new JSONConverter()

	public NewIFMBatch loadAndAdjust(Long taskId, LocalDateTime startTime, String resourceName) {
		List<Object> batchActivityList = loadObjects(resourceName)
		adjustTaskId(batchActivityList, taskId)
		adjustTimeAndMakeConsecutive(batchActivityList, startTime)

		return convertToBatch(batchActivityList)
	}

	public List<Object> loadAndAdjustToConsecutiveTime(String resourceName, Long taskId, LocalDateTime startTime) {
		List<Object> batchActivityList = loadObjects(resourceName)
		adjustTaskId(batchActivityList, taskId)
		adjustTimeAndMakeConsecutive(batchActivityList, startTime)

		return batchActivityList
	}

	private List<Object> loadObjects(String resourceName) {
		List<Object> batchActivityList = []

		URL resource = this.getClass().getResource(resourceName)

		if (resource) {
			resource.withReader { r ->
				List<String> jsonLines = r.readLines()
				jsonLines.each { String line ->
					Object object = jsonConverter.fromJSON(line)
					batchActivityList.add(object)

				}
			}
		}
		return batchActivityList
	}

	public void writeRawBatchActivityList(File fileToWrite, List<Object> objectsToWrite) {
		objectsToWrite.each { Object o ->
			fileToWrite.append(jsonConverter.toJSON(o) + "\n")
		}
	}


	NewIFMBatch convertToBatch(List<Object> batchActivityList) {
		NewIFMBatch batch = createEmptyBatch()
		batchActivityList.each { Object o ->
			addObjectToBatch(batch, o)
		}
		return batch
	}


	private NewIFMBatch createEmptyBatch() {
		NewIFMBatch.builder()
				.timeSent(LocalDateTime.now())
				.editorActivityList([])
				.externalActivityList([])
				.idleActivityList([])
				.executionActivityList([])
				.modificationActivityList([])
				.blockActivityList([])
				.eventList([])
				.build()
	}


	private void addObjectToBatch(NewIFMBatch batch, Object object) {
		if (object instanceof NewEditorActivity) {
			batch.editorActivityList.add(object)
		} else if (object instanceof NewExternalActivity) {
			batch.externalActivityList.add(object)
		} else if (object instanceof NewIdleActivity) {
			batch.idleActivityList.add(object)
		} else if (object instanceof NewExecutionActivity) {
			batch.executionActivityList.add(object)
		} else if (object instanceof NewModificationActivity) {
			batch.modificationActivityList.add(object)
		} else if (object instanceof NewBlockActivity) {
			batch.blockActivityList.add(object)
		} else if (object instanceof NewBatchEvent) {
			batch.eventList.add(object)
		}
	}

	private void adjustTaskId(List<Object> activityObjects, Long taskId) {
		activityObjects.each { Object o ->
			o.taskId = taskId
		}
	}

	private void adjustTimeAndMakeConsecutive(List<Object> activityObjects, LocalDateTime startTime) {
		long relativeTime = 0
		int seconds = 0
		LocalDateTime currentTime = startTime.plusSeconds(0)

		activityObjects.each { Object o ->
			if (o.hasProperty("durationInSeconds")) {
				seconds = o.durationInSeconds
				relativeTime += seconds
				currentTime = currentTime.plusSeconds(seconds)

			}
			if (o.hasProperty("position")) {
				o.position = currentTime
			}

			if (o.hasProperty("endTime")) {
				o.endTime = currentTime.plusSeconds(seconds)
			}
		}
	}
}

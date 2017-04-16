package org.openmastery.publisher.ideaflow.story

import org.openmastery.publisher.api.ideaflow.ActivitySummary
import org.openmastery.publisher.api.ideaflow.Haystack

import java.time.LocalDateTime

class HaystackListValidator {

	private int index = 0
	private LocalDateTime startTime
	private List<Haystack> haystackList
	private HaystackValidator lastValidatorCreated

	HaystackListValidator(List<Haystack> haystackList, LocalDateTime startTime) {
		this.haystackList = haystackList
		this.startTime = startTime
	}

	HaystackValidator firstValidator() {
		createHaystackValidator()
				.assertFirstHaystack(startTime)
	}

	HaystackValidator nextValidatorAtRelativePositionInMinutes(int relativePositionInMinutes) {
		nextValidatorAtRelativePositionInMinutes(startTime.plusMinutes(relativePositionInMinutes), relativePositionInMinutes)
	}

	HaystackValidator nextValidatorAtRelativePositionInMinutes(LocalDateTime position, int relativePositionInMinutes) {
		createHaystackValidator()
				.assertPosition(position)
				.assertRelativePositionInMinutes(relativePositionInMinutes)
	}

	private HaystackValidator createHaystackValidator() {
		if (index >= haystackList.size()) {
			assert false: "Not enough haystacks"
		}
		if (lastValidatorCreated != null) {
			lastValidatorCreated.assertValidationComplete()
		}
		lastValidatorCreated = new HaystackValidator(haystackList[index++])
		lastValidatorCreated
	}

	void assertValidationComplete() {
		lastValidatorCreated.assertValidationComplete()
		assert haystackList.size() == index
	}

	public static class HaystackValidator {

		private Haystack haystack
		private int activityIndex

		HaystackValidator(Haystack haystack) {
			this.haystack = haystack
		}

		HaystackValidator assertRelativePath(String expectedRelativePath) {
			assert haystack.relativePath == expectedRelativePath
			this
		}

		HaystackValidator assertPosition(LocalDateTime position) {
			assert haystack.position == position
			this
		}

		HaystackValidator assertRelativePositionInMinutes(int relativePositionInMinutes) {
			assert haystack.relativePositionInSeconds == (relativePositionInMinutes * 60L)
			this
		}

		HaystackValidator assertDurationInMinutes(int durationInMinutes) {
			assert haystack.durationInSeconds == (durationInMinutes * 60L)
			this
		}

		HaystackValidator assertExecutionDurationInMinutes(Long executionDurationInMinutes) {
			assert haystack.executionDurationInSeconds == (executionDurationInMinutes * 60L)
			this
		}

		HaystackValidator assertFirstHaystack(LocalDateTime expectedPosition) {
			assert haystack.processName == null
			assert haystack.relativePath == null
			assert haystack.debug == false
			assert haystack.failed == false
			assert haystack.relativePositionInSeconds == 0L
			assert haystack.executionDurationInSeconds == 0L
			assert haystack.position == expectedPosition
			this
		}

		HaystackValidator assertProcessName(String processName) {
			assert haystack.processName == processName
			this
		}

		HaystackValidator assertExecutionTaskType(String executionTaskType) {
			assert haystack.executionTaskType == executionTaskType
			this
		}

		HaystackValidator assertDebug(boolean debug) {
			assert haystack.debug == debug
			this
		}

		HaystackValidator assertFailed(boolean failed) {
			assert haystack.failed == failed
			this
		}

		HaystackValidator assertExternalActivityDurationInMinutes(Long totalDurationInMinutes) {
			ActivitySummary summary = haystack.activitySummaries.find { ActivitySummary summary ->
				summary.activityType == HaystackListGenerator.ACTIVITY_TYPE_EXTERNAL
			}
			assert summary != null : "No external summary, activitySummaries=${haystack.activitySummaries}"
			assert summary.totalDurationInSeconds == (totalDurationInMinutes * 60L)
			assert summary.modifiedDurationInSeconds == 0L
			haystack.activitySummaries.remove(summary)
			this
		}

		HaystackValidator assertFileActivityDurationInMinutes(String filePath, Long totalDurationInMinutes, Long modifiedDurationInMinutes) {
			ActivitySummary summary = haystack.activitySummaries.find { ActivitySummary summary ->
				summary.activityType == HaystackListGenerator.ACTIVITY_TYPE_EDITOR && summary.activityDetail == filePath
			}
			assert summary != null : "No editor summary with filePath=${filePath}, activitySummaries=${haystack.activitySummaries}"
			assert summary.totalDurationInSeconds == (totalDurationInMinutes * 60L)
			assert summary.modifiedDurationInSeconds == (modifiedDurationInMinutes * 60L)
			assert summary.activityName == new File(filePath).name
			haystack.activitySummaries.remove(summary)
			this
		}

		void assertValidationComplete() {
			assert haystack.activitySummaries.isEmpty()
		}

	}

}

package org.openmastery.publisher.ideaflow.story

import org.openmastery.publisher.api.ideaflow.Haystack
import org.openmastery.publisher.core.EntityListBuilder
import org.openmastery.publisher.ideaflow.timeline.IdleTimeProcessor
import org.openmastery.publisher.ideaflow.timeline.RelativeTimeProcessor
import spock.lang.Ignore
import spock.lang.Specification

import java.time.LocalDateTime

class HaystackListGeneratorSpec extends Specification {

	EntityListBuilder entityListBuilder = new EntityListBuilder()
	LocalDateTime startTime = entityListBuilder.startTime
	HaystackListGenerator haystackListGenerator = new HaystackListGenerator(new RelativeTimeProcessor(), new IdleTimeProcessor())

	private HaystackListValidator generateHaystacksAndCreateValidator() {
		List<Haystack> haystackList = haystackListGenerator
				.taskStart(startTime)
				.editorActivities(entityListBuilder.editorActivityEntityList)
				.idleActivities(entityListBuilder.idleActivityEntityList)
				.executionActivities(entityListBuilder.executionActivityList)
				.externalActivities(entityListBuilder.externalActivityList)
				.events(entityListBuilder.eventList)
				.generate()
		new HaystackListValidator(haystackList, startTime)
	}

	def "should correctly record relativePath and processName"() {
		given:
		entityListBuilder
				.viewFileAndAdvanceMinutes(5, "path/file")
				.execute(1, "process")
		entityListBuilder.executionActivityList[0].id = 3

		when:
		HaystackListValidator validator = generateHaystacksAndCreateValidator()

		then:
		validator.firstValidator()
				.assertExecutionDurationInMinutes(1)
				.assertProcessName("process")
				.assertRelativePath("/haystack/3")
				.assertFileActivityDurationInMinutes("path/file", 5, 0)
		validator.assertValidationComplete()
	}

	def "should create no haystacks if no execution events"() {
		given:
		entityListBuilder
				.viewFileAndAdvanceMinutes(5, "path/file")
				.idleActivity(5)
				.switchToExternalApp(2, "app")
				.modifyFileAndAdvanceMinutes(5, "path/file")

		when:
		HaystackListValidator validator = generateHaystacksAndCreateValidator()

		then:
		assert validator.haystackList.size() == 0
	}

	def "should split editor activity by execution"() {
		given:
		entityListBuilder
				.modifyFile(11, "path/file")
				.advanceMinutes(5)
				.execute(1, "process")
				.advanceMinutes(6)
				.execute(0, "final")

		when:
		HaystackListValidator validator = generateHaystacksAndCreateValidator()

		then:
		validator.firstValidator()
				.assertDurationInMinutes(5)
				.assertExecutionDurationInMinutes(1)
				.assertFileActivityDurationInMinutes("path/file", 5, 5)
		validator.nextValidatorAtRelativePositionInMinutes(5)
				.assertDurationInMinutes(6)
				.assertExecutionDurationInMinutes(0)
				.assertFileActivityDurationInMinutes("path/file", 6, 6)
		validator.assertValidationComplete()
	}

	def "should aggregate file activity"() {
		given:
		entityListBuilder
				.viewFileAndAdvanceMinutes(5, "path/file1")
				.modifyFileAndAdvanceMinutes(5, "path/file2")
				.debug(8, "process")
				.modifyFileAndAdvanceMinutes(6, "path/file1")
				.viewFileAndAdvanceMinutes(6, "path/file2")
				.execute(1, "final")

		when:
		HaystackListValidator validator = generateHaystacksAndCreateValidator()

		then:
		validator.firstValidator()
				.assertDurationInMinutes(10)
				.assertExecutionDurationInMinutes(8)
				.assertFileActivityDurationInMinutes("path/file1", 5, 0)
				.assertFileActivityDurationInMinutes("path/file2", 5, 5)
		validator.nextValidatorAtRelativePositionInMinutes(10)
				.assertDurationInMinutes(12)
				.assertExecutionDurationInMinutes(1)
				.assertFileActivityDurationInMinutes("path/file1", 6, 6)
				.assertFileActivityDurationInMinutes("path/file2", 6, 0)
		validator.assertValidationComplete()
	}

	def "should aggregate external activity"() {
		given:
		entityListBuilder
				.switchToExternalApp(2, "app1")
				.viewFileAndAdvanceMinutes(10, "path/file1")
				.switchToExternalApp(3, "app2")
				.execute(2, "process")
				.switchToExternalApp(4, "app1")
				.modifyFileAndAdvanceMinutes(5, "path/file2")
				.switchToExternalApp(5, "app2")
				.execute(1, "final")

		when:
		HaystackListValidator validator = generateHaystacksAndCreateValidator()

		then:
		validator.firstValidator()
				.assertDurationInMinutes(15)
				.assertExecutionDurationInMinutes(2)
				.assertExternalActivityDurationInMinutes(5)
				.assertFileActivityDurationInMinutes("path/file1", 10, 0)
		validator.nextValidatorAtRelativePositionInMinutes(15)
				.assertDurationInMinutes(14)
				.assertExecutionDurationInMinutes(1)
				.assertExternalActivityDurationInMinutes(9)
				.assertFileActivityDurationInMinutes("path/file2", 5, 5)
		validator.assertValidationComplete()
	}

	@Ignore // TODO: implement
	def "should exclude idle time from durations"() {
		given:
		entityListBuilder
				.modifyFile(15, "path/file1")
				.advanceMinutes(2)
				.idleActivity(10)
				.executeAndAdvanceMinutes(4, "process")
				.idleActivity(7)
				.viewFileAndAdvanceMinutes(3, "path/file1")

		when:
		HaystackListValidator validator = generateHaystacksAndCreateValidator()

		then:
		validator.firstValidator()
				.assertDurationInMinutes(2)
				.assertFileActivityDurationInMinutes("path/file1", 2, 2)
		validator.nextValidatorAtRelativePositionInMinutes(startTime.plusMinutes(12), 2)
				.assertDurationInMinutes(7)
				.assertExecutionDurationInMinutes(4)
				.assertFileActivityDurationInMinutes("path/file1", 6, 6)
		validator.assertValidationComplete()
	}

}

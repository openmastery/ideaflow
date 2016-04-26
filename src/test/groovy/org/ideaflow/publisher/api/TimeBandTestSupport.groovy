package org.ideaflow.publisher.api

import groovy.transform.Trait
import org.ideaflow.publisher.core.MockTimeService
import org.ideaflow.publisher.core.activity.IdleActivityEntity

import java.lang.annotation.Annotation
import java.time.LocalDateTime


trait TimeBandTestSupport {

	IdeaFlowBand createBand(LocalDateTime start, LocalDateTime end) {
		IdeaFlowBand.builder()
				.start(start)
				.end(end)
				.nestedBands([])
				.build()
	}

	IdleActivityEntity createIdle(LocalDateTime start, LocalDateTime end) {
		IdleActivityEntity.builder()
				.start(start)
				.end(end)
				.build()
	}


	TimeBandGroup createGroup(LocalDateTime start, LocalDateTime end) {
		createGroup(createBand(start, end))
	}

	TimeBandGroup createGroup(IdeaFlowBand... bands) {
		TimeBandGroup.builder()
				.linkedTimeBands(bands as List)
				.build()
	}

	void assertStartAndEnd(TimeBand band, LocalDateTime expectedStart, LocalDateTime expectedEnd) {
		assert band.start == expectedStart
		assert band.end == expectedEnd
	}

}

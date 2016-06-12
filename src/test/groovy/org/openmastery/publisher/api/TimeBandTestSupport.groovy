package org.openmastery.publisher.api

import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.publisher.core.timeline.TimeBandModel
import org.openmastery.publisher.core.timeline.TimeBandGroupModel

import java.time.LocalDateTime


trait TimeBandTestSupport {

	IdeaFlowBandModel createBand(LocalDateTime start, LocalDateTime end) {
		IdeaFlowBandModel.builder()
				.start(start)
				.end(end)
				.idleBands([])
				.nestedBands([])
				.build()
	}

	IdleTimeBandModel createIdle(LocalDateTime start, LocalDateTime end) {
		IdleTimeBandModel.builder()
				.start(start)
				.end(end)
				.build()
	}


	TimeBandGroupModel createGroup(LocalDateTime start, LocalDateTime end) {
		createGroup(createBand(start, end))
	}

	TimeBandGroupModel createGroup(IdeaFlowBandModel... bands) {
		TimeBandGroupModel.builder()
				.linkedTimeBands(bands as List)
				.build()
	}

	void assertStartAndEnd(TimeBandModel band, LocalDateTime expectedStart, LocalDateTime expectedEnd) {
		assert band.start == expectedStart
		assert band.end == expectedEnd
	}

}

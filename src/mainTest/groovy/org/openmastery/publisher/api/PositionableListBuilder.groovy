package org.openmastery.publisher.api

import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.time.MockTimeService


class PositionableListBuilder {

	private MockTimeService timeService
	private List positionables = []

	PositionableListBuilder() {
		this(new MockTimeService())
	}

	PositionableListBuilder(MockTimeService timeService) {
		this.timeService = timeService
	}

	PositionableListBuilder position(int position) {
		positionables << PositionableImpl.builder()
				.position(timeService.inFuture(position))
				.build()
		this
	}

	PositionableListBuilder interval(int startHour, int endHour) {
		positionables << IntervalImpl.builder()
				.start(timeService.inFuture(startHour))
				.end(timeService.inFuture(endHour))
				.build()
		this
	}

	PositionableListBuilder idle(int startHour, int endHour) {
		positionables << IdleTimeBandModel.builder()
				.start(timeService.inFuture(startHour))
				.end(timeService.inFuture(endHour))
				.build()
		this
	}

	List<Positionable> build() {
		List positionablesToReturn = positionables
		positionables = []
		positionablesToReturn
	}

}

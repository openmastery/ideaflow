package org.openmastery.publisher.api

import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.time.MockTimeService


class PositionableAndIntervalListBuilder {

	private MockTimeService timeService
	private List positionables = []

	PositionableAndIntervalListBuilder() {
		this(new MockTimeService())
	}

	PositionableAndIntervalListBuilder(MockTimeService timeService) {
		this.timeService = timeService
	}

	PositionableAndIntervalListBuilder advanceDays(int days) {
		timeService.plusDays(days)
		this
	}

	PositionableAndIntervalListBuilder position(int position) {
		positionables << PositionableImpl.builder()
				.position(timeService.inFuture(position))
				.build()
		this
	}

	PositionableAndIntervalListBuilder interval(int startHour, int endHour) {
		positionables << IntervalImpl.builder()
				.start(timeService.inFuture(startHour))
				.end(timeService.inFuture(endHour))
				.build()
		this
	}

	PositionableAndIntervalListBuilder idle(int startHour, int endHour) {
		positionables << IdleTimeBandModel.builder()
				.start(timeService.inFuture(startHour))
				.end(timeService.inFuture(endHour))
				.build()
		this
	}

	List<Positionable> buildPositionables() {
		List positionablesToReturn = positionables
		positionables = []
		positionablesToReturn
	}

	List<Interval> buildIntervals() {
		List positionablesToReturn = positionables
		positionables = []
		positionablesToReturn.findAll {
			it instanceof Interval
		}
	}

}

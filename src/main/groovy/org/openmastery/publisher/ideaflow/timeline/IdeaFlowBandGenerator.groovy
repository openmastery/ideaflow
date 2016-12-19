package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.Interval
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.api.activity.ModificationActivity
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel

class IdeaFlowBandGenerator {

	private int learningTimeThresholdInMinutes = 5
	private int learningModificationCountThreshold = 150

	public List<IdeaFlowBandModel> generateIdeaFlowBands(List<Positionable> positionableList) {
		if (positionableList.isEmpty()) {
			return []
		}
		positionableList = positionableList.sort(false, PositionableComparator.INSTANCE)

		List<IdeaFlowBandModel> strategyBands = generateStrategyBands(positionableList)
		List<IdeaFlowBandModel> troubleshootingBands = generateTroubleshootingBands(positionableList)
		List<IdeaFlowBandModel> ideaFlowBandList = strategyBands + troubleshootingBands
		ideaFlowBandList.sort(PositionableComparator.INSTANCE)

		List progressBands = []
		LocalDateTime lastBandEndTime = positionableList.first().position
		for (IdeaFlowBandModel ideaFlowBandModel : ideaFlowBandList) {
			if (ideaFlowBandModel.start.isAfter(lastBandEndTime)) {
				progressBands << createIdeaFlowBand(lastBandEndTime, ideaFlowBandModel.start, IdeaFlowStateType.PROGRESS)
			}
			lastBandEndTime = ideaFlowBandModel.end
		}

		ideaFlowBandList.addAll(progressBands)
		ideaFlowBandList.sort(PositionableComparator.INSTANCE)
		ideaFlowBandList
	}

	private List<IdeaFlowBandModel> generateStrategyBands(List<Positionable> sortedPositionableList) {
		List<IdeaFlowBandModel> learningBandList = []

		LocalDateTime learningBandStartTime = sortedPositionableList.first().position
		ModificationActivityTracker tracker = new ModificationActivityTracker(learningTimeThresholdInMinutes, learningModificationCountThreshold)
		for (Positionable positionable : sortedPositionableList) {
			tracker.addModificationActivity(positionable)
			if (tracker.isOverModificationThreshold()) {
				if (learningBandStartTime != null) {
					learningBandList << createIdeaFlowBand(learningBandStartTime, tracker.earliestTrackedTime, IdeaFlowStateType.LEARNING)
					learningBandStartTime = null
				}
			} else if (learningBandStartTime == null) {
				learningBandStartTime = tracker.earliestTrackedTime
			}
		}

		if (learningBandStartTime != null) {
			learningBandList << createIdeaFlowBand(learningBandStartTime, tracker.latestTrackedTime, IdeaFlowStateType.LEARNING)
		}

		learningBandList.removeAll { IdeaFlowBandModel ideaFlowBandModel ->
			ideaFlowBandModel.start.plusMinutes(learningTimeThresholdInMinutes).isAfter(ideaFlowBandModel.end)
		}
		learningBandList
	}

	private List<IdeaFlowBandModel> generateTroubleshootingBands(List<Positionable> sortedPositionableList) {
		List<IdeaFlowBandModel> troubleshootingBandList = []
		LocalDateTime troubleshootingStart = null
		List<Event> troubleshootingEvents = getTroubleshootingEvents(sortedPositionableList)
		for (Event event : troubleshootingEvents) {
			if (event.type == EventType.WTF) {
				if (troubleshootingStart == null) {
					troubleshootingStart = event.position
				}
			} else if (event.type == EventType.AWESOME) {
				if (troubleshootingStart != null) {
					troubleshootingBandList << createIdeaFlowBand(troubleshootingStart, event.position, IdeaFlowStateType.TROUBLESHOOTING)
				}
			}
		}
		troubleshootingBandList
	}

	private List<Event> getTroubleshootingEvents(List<Positionable> sortedPositionableList) {
		sortedPositionableList.findAll {
			EventType eventType = null
			if (it instanceof Event) {
				eventType = ((Event) it).type
			}
			eventType == EventType.WTF || eventType == EventType.AWESOME
		}
	}


	private IdeaFlowBandModel createIdeaFlowBand(LocalDateTime start, LocalDateTime end, IdeaFlowStateType type) {
		IdeaFlowBandModel.builder()
				.start(start)
				.end(end)
				.type(type)
				.nestedBands([])
				.idleBands([])
				.build()
	}


	private static class ModificationActivityTracker {

		private int timeThresholdInMinutes
		private int modificationCountThreshold
		private List<Positionable> recentActivityList = []

		ModificationActivityTracker(int timeThresholdInMinutes, int modificationCountThreshold) {
			this.timeThresholdInMinutes = timeThresholdInMinutes
			this.modificationCountThreshold = modificationCountThreshold
		}

		void addModificationActivity(Positionable positionable) {
			LocalDateTime minimumPosition = positionable.position.minusMinutes(timeThresholdInMinutes)

			recentActivityList.removeAll { Positionable p ->
				p.position.isBefore(minimumPosition)
			}
			recentActivityList.add(positionable)
		}

		boolean isOverModificationThreshold() {
			int modificationCount = recentActivityList.sum { Positionable positionable ->
				(positionable instanceof ModificationActivity) ? positionable.modificationCount : 0
			}
			modificationCount > modificationCountThreshold
		}

		LocalDateTime getEarliestTrackedTime() {
			recentActivityList.collect { Positionable pos ->
				pos.position
			}.sort().first()
		}

		LocalDateTime getLatestTrackedTime() {
			recentActivityList.collect { Positionable pos ->
				(pos instanceof Interval) ? pos.end : pos.position
			}.sort().last()
		}

	}

}

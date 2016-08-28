package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.api.timeline.ActivityNode
import org.openmastery.publisher.api.timeline.ActivityNodeType
import org.openmastery.publisher.api.timeline.ActivityTimeline


class TimelinePrettyPrinter {

	static void printTimeline(ActivityTimeline activityTimeline) {
		activityTimeline.activityNodes.eachWithIndex { ActivityNode activityNode, i ->
			print "activityNode[$i] : ${activityNode.type} : ${activityNode.relativePositionInSeconds} : "
			if (activityNode.type == ActivityNodeType.BAND) {
				println "{type=${activityNode.bandStateType}, bandStart=${activityNode.bandStart}, comment='${activityNode.bandComment}'}"
			}
			else if (activityNode.type == ActivityNodeType.EDITOR) {
				println "{name='${activityNode.editorFileName}', isModified=${activityNode.editorFileIsModified}, path='${activityNode.editorFilePath}', duration=${activityNode.editorDurationInSeconds}}"
			}
			else if (activityNode.type == ActivityNodeType.EXTERNAL) {
				println "{isIdle=${activityNode.externalIdle}, duration=${activityNode.externalDurationInSeconds}}"
			}
			else if (activityNode.type == ActivityNodeType.EVENT) {
				println "(comment='${activityNode.eventComment}'}"
			}
		}
	}
}

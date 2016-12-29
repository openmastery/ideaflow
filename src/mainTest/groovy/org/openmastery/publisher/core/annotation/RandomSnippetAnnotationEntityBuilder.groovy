package org.openmastery.publisher.core.annotation

import static org.openmastery.publisher.ARandom.aRandom

class RandomSnippetAnnotationEntityBuilder extends SnippetAnnotationEntity.SnippetAnnotationEntityBuilder {
	RandomSnippetAnnotationEntityBuilder() {
		super.id(aRandom.id())
				.ownerId(aRandom.id())
				.taskId(aRandom.id())
				.eventId(aRandom.id())
				.source(aRandom.text(10))
				.snippet(aRandom.text(100))
	}
}

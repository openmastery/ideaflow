package org.openmastery.publisher.core.activity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("external")
@Data
@Builder
@EqualsAndHashCode(callSuper = true, of = {})
public class ExternalActivityEntity extends ActivityEntity {

	private static final String COMMENT_KEY = "comment";

	private ExternalActivityEntity() {}

	private ExternalActivityEntity(long id, long taskId, LocalDateTime start, LocalDateTime end, String comment) {
		super(id, taskId, start, end);
		setComment(comment);
	}

	public String getComment() {
		return getMetadataValue(COMMENT_KEY);
	}

	public void setComment(String comment) {
		setMetadataField(COMMENT_KEY, comment);
	}


	public static ExternalActivityEntityBuilder builder() {
		return new ExternalActivityEntityBuilder();
	}

	public static class ExternalActivityEntityBuilder extends ActivityEntityBuilder<ExternalActivityEntityBuilder> {

		private String comment;

		public ExternalActivityEntity build() {
			return new ExternalActivityEntity(id, taskId, start, end, comment);
		}

		public ExternalActivityEntityBuilder comment(String comment) {
			this.comment = comment;
			return this;
		}

	}
	
}

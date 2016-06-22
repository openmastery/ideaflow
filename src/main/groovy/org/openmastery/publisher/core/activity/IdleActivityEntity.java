package org.openmastery.publisher.core.activity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("idle")
@Data
@EqualsAndHashCode(callSuper = true, of = {})
public class IdleActivityEntity extends ActivityEntity {

	private static final String COMMENT_KEY = "comment";
	private static final String AUTO_KEY = "auto";

	private IdleActivityEntity() {}

	private IdleActivityEntity(long id, long taskId, LocalDateTime start, LocalDateTime end, String comment, boolean auto) {
		super(id, taskId, start, end);
		setComment(comment);
		setAuto(auto);
	}

	public String getComment() {
		return getMetadataValue(COMMENT_KEY);
	}

	public void setComment(String comment) {
		setMetadataField(COMMENT_KEY, comment);
	}

	public boolean isAuto() {
		return getMetadataValueAsBoolean(AUTO_KEY);
	}

	public void setAuto(boolean auto) {
		setMetadataField(AUTO_KEY, auto);
	}


	public static IdleActivityEntityBuilder builder() {
		return new IdleActivityEntityBuilder();
	}

	public static class IdleActivityEntityBuilder extends ActivityEntityBuilder<IdleActivityEntityBuilder> {

		private String comment;
		private boolean auto;

		public IdleActivityEntity build() {
			return new IdleActivityEntity(id, taskId, start, end, comment, auto);
		}

		public IdleActivityEntityBuilder comment(String comment) {
			this.comment = comment;
			return this;
		}

		public IdleActivityEntityBuilder isAuto(boolean auto) {
			this.auto = auto;
			return this;
		}
	}

}

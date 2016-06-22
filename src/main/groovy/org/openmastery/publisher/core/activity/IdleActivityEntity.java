package org.openmastery.publisher.core.activity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, of = {})
public class IdleActivityEntity extends ActivityEntity {

	@Transient
	private String comment;
	@Transient
	private boolean auto;

	private IdleActivityEntity() {}

	private IdleActivityEntity(long id, long taskId, LocalDateTime start, LocalDateTime end, String comment, boolean auto) {
		super(id, taskId, start, end);
		this.comment = comment;
		this.auto = auto;
	}


	public static IdleActivityEntityBuilder builder() {
		return new IdleActivityEntityBuilder();
	}

	public static class IdleActivityEntityBuilder {

		private long id;
		private long taskId;
		private LocalDateTime start;
		private LocalDateTime end;
		private String comment;
		private boolean auto;

		public IdleActivityEntity build() {
			return new IdleActivityEntity(id, taskId, start, end, comment, auto);
		}

		public IdleActivityEntityBuilder id(long id) {
			this.id = id;
			return this;
		}

		public IdleActivityEntityBuilder taskId(long taskId) {
			this.taskId = taskId;
			return this;
		}

		public IdleActivityEntityBuilder start(LocalDateTime start) {
			this.start = start;
			return this;
		}

		public IdleActivityEntityBuilder end(LocalDateTime end) {
			this.end = end;
			return this;
		}

		public IdleActivityEntityBuilder comment(String comment) {
			this.comment = comment;
			return this;
		}

		public IdleActivityEntityBuilder auto(boolean auto) {
			this.auto = auto;
			return this;
		}
	}

}

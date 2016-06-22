package org.openmastery.publisher.core.activity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, of = {})
public class EditorActivityEntity extends ActivityEntity {

	@Transient
	private String filePath;
	@Transient
	private boolean modified;

	private EditorActivityEntity() {}

	private EditorActivityEntity(long id, long taskId, LocalDateTime start, LocalDateTime end, String filePath, boolean modified) {
		super(id, taskId, start, end);
		this.filePath = filePath;
		this.modified = modified;
	}


	public static EditorActivityEntityBuilder builder() {
		return new EditorActivityEntityBuilder();
	}

	public static class EditorActivityEntityBuilder {

		private long id;
		private long taskId;
		private LocalDateTime start;
		private LocalDateTime end;
		private String filePath;
		private boolean modified;

		public EditorActivityEntity build() {
			return new EditorActivityEntity(id, taskId, start, end, filePath, modified);
		}

		public EditorActivityEntityBuilder id(long id) {
			this.id = id;
			return this;
		}

		public EditorActivityEntityBuilder taskId(long taskId) {
			this.taskId = taskId;
			return this;
		}

		public EditorActivityEntityBuilder start(LocalDateTime start) {
			this.start = start;
			return this;
		}

		public EditorActivityEntityBuilder end(LocalDateTime end) {
			this.end = end;
			return this;
		}

		public EditorActivityEntityBuilder filePath(String filePath) {
			this.filePath = filePath;
			return this;
		}

		public EditorActivityEntityBuilder modified(boolean modified) {
			this.modified = modified;
			return this;
		}
	}

}

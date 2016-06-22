package org.openmastery.publisher.core.activity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Slf4j
@Entity(name = "activity")
@Inheritance
@DiscriminatorColumn(name = "type")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public abstract class ActivityEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "activity_seq_gen")
	@SequenceGenerator(name = "activity_seq_gen", sequenceName = "activity_seq")
	private long id;
	private long taskId;

	@Column(name = "start_time")
	private LocalDateTime start;
	@Column(name = "end_time")
	private LocalDateTime end;

	private String metadata;
	@Transient
	private MetadataContainer metadataContainer = new MetadataContainer();

	public ActivityEntity() {
	}

	protected ActivityEntity(long id, long taskId, LocalDateTime start, LocalDateTime end) {
		this.id = id;
		this.taskId = taskId;
		this.start = start;
		this.end = end;
	}

	protected void setMetadataField(String key, Object value) {
		metadataContainer.setMetadataField(key, value);
		// NOTE: ideally would want to use @PrePersist but it seems that on save, if the object is being updated,
		// a copy of the object is made to be persisted; this causes issues since the metadataContainer is not
		// replicated to the copy... there's probably some cleaner way to resolve this but this works for now
		metadata = metadataContainer.toJson();
	}

	protected boolean getMetadataValueAsBoolean(String key) {
		return metadataContainer.getMetadataValueAsBoolean(key);
	}

	protected String getMetadataValue(String key) {
		return metadataContainer.getMetadataValue(key);
	}

	@PostLoad
	private void postLoad() {
		metadataContainer.fromJson(metadata);
	}


	public static abstract class ActivityEntityBuilder<T extends ActivityEntityBuilder> {

		protected long id;
		protected long taskId;
		protected LocalDateTime start;
		protected LocalDateTime end;

		public T id(long id) {
			this.id = id;
			return (T) this;
		}

		public T taskId(long taskId) {
			this.taskId = taskId;
			return (T) this;
		}

		public T start(LocalDateTime start) {
			this.start = start;
			return (T) this;
		}

		public T end(LocalDateTime end) {
			this.end = end;
			return (T) this;
		}

	}

}

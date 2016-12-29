/**
 * Copyright 2016 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.core.annotation;


import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.openmastery.publisher.api.event.EventType;
import org.openmastery.publisher.core.activity.MetadataContainer;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Entity(name = "annotation")
@Inheritance
@DiscriminatorColumn(name = "type")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public abstract class AnnotationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "annotation_seq_gen")
	@SequenceGenerator(name = "annotation_seq_gen", sequenceName = "annotation_seq")

	private Long id;
	private Long taskId;
	private Long ownerId;
	private Long eventId;

	private String metadata;
	@Transient
	private MetadataContainer metadataContainer = new MetadataContainer();

	public AnnotationEntity() {
	}

	protected AnnotationEntity(long id, long ownerId, long taskId, long eventId) {
		this.id = id;
		this.ownerId = ownerId;
		this.taskId = taskId;
		this.eventId = eventId;
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

	protected int getMetadataValueAsInteger(String key) {
		return metadataContainer.getMetadataValueAsInteger(key);
	}

	protected String getMetadataValue(String key) {
		return metadataContainer.getMetadataValue(key);
	}

	@PostLoad
	private void postLoad() {
		metadataContainer.fromJson(metadata);
	}

	public static abstract class AnnotationEntityBuilder<T extends AnnotationEntityBuilder> {

		protected long id;
		protected long ownerId;
		protected long taskId;
		protected long eventId;

		public T id(long id) {
			this.id = id;
			return (T) this;
		}

		public T ownerId(long ownerId) {
			this.ownerId = ownerId;
			return (T) this;
		}

		public T taskId(long taskId) {
			this.taskId = taskId;
			return (T) this;
		}

		public T eventId(long eventId) {
			this.eventId = eventId;
			return (T) this;
		}

	}


}


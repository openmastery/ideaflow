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

	private ExternalActivityEntity() {}

	private ExternalActivityEntity(long id, long taskId, LocalDateTime start, LocalDateTime end) {
		super(id, taskId, start, end);
	}


	public static ExternalActivityEntityBuilder builder() {
		return new ExternalActivityEntityBuilder();
	}

	public static class ExternalActivityEntityBuilder extends ActivityEntityBuilder<ExternalActivityEntityBuilder> {

		public ExternalActivityEntity build() {
			return new ExternalActivityEntity(id, taskId, start, end);
		}

	}
	
}

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

	private IdleActivityEntity() {}

	private IdleActivityEntity(long id, long taskId, LocalDateTime start, LocalDateTime end) {
		super(id, taskId, start, end);
	}


	public static IdleActivityEntityBuilder builder() {
		return new IdleActivityEntityBuilder();
	}

	public static class IdleActivityEntityBuilder extends ActivityEntityBuilder<IdleActivityEntityBuilder> {

		public IdleActivityEntity build() {
			return new IdleActivityEntity(id, taskId, start, end);
		}

	}

}

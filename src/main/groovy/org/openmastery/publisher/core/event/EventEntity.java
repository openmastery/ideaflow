package org.openmastery.publisher.core.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.event.EventType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.LocalDateTime;

@Entity(name = "event")
@Data
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "event_seq_gen")
	@SequenceGenerator(name = "event_seq_gen", sequenceName = "event_seq")
	private Long id;
	private Long taskId;

	private LocalDateTime position;

	private String comment;

	@Enumerated(EnumType.STRING)
	private EventType type;

}

package org.openmastery.publisher.core.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.LocalDateTime;

@Entity(name = "idle_time_band")
@Data
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdleTimeBandEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "idle_time_band_seq_gen")
	@SequenceGenerator(name = "idle_time_band_seq_gen", sequenceName = "idle_time_band_seq")
	private long id;
	private long taskId;

	@Column(name = "start_time")
	private LocalDateTime start;
	@Column(name = "end_time")
	private LocalDateTime end;

	private String comment;

	private boolean auto;

}

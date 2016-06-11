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
import javax.persistence.Transient;
import java.io.File;
import java.time.LocalDateTime;

@Entity(name = "editor_activity")
@Data
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditorActivityEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "editor_activity_seq_gen")
	@SequenceGenerator(name = "editor_activity_seq_gen", sequenceName = "editor_activity_seq")
	private long id;
	private long taskId;

	@Column(name = "start_time")
	private LocalDateTime start;
	@Column(name = "end_time")
	private LocalDateTime end;

	private String filePath;
	private boolean isModified;

	@Transient
	public String getFileName() {
		return new File(filePath).getName();
	}

//	@Transient
//	private Duration duration;
//
//	@Column(name = "duration")
//	private long durationInSeconds;
//
//	@PostLoad
//	public void init() {
//		duration = Duration.ofSeconds(durationInSeconds);
//	}
//
//	public void setDuration(Duration duration) {
//		this.duration = duration;
//		if (duration != null) {
//			durationInSeconds = duration.getSeconds();
//		}
//	}
}

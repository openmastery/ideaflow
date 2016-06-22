package org.openmastery.publisher.core.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Properties;

@Entity(name = "activity")
@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class ActivityEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "activity_seq_gen")
	@SequenceGenerator(name = "activity_seq_gen", sequenceName = "activity_seq")
	private long id;
	private long taskId;

	@Column(name = "start_time")
	private LocalDateTime start;
	@Column(name = "end_time")
	private LocalDateTime end;

	@Transient
	private Properties metadataContainer;

	public ActivityEntity() {}

	protected ActivityEntity(long id, long taskId, LocalDateTime start, LocalDateTime end) {
		this.id = id;
		this.taskId = taskId;
		this.start = start;
		this.end = end;
	}

//	public String getMetadata() {
//		new ObjectMapper().writeValueAsString()
//				readValue("", HashMap.class);
//	}
//
//	public void setMetadata(String metadata) {
//
//		new ObjectMapper().readValue("", HashMap.class);
////		if (metadata == null) {
////			metadataContainer = new Properties();
////		} else {
////			new Properties().
////			metadataContainer
////		}
//	}
//
//	protected void setMetadataValue(String key, String value) {
//
//	}
//
//	protected String getMetadataValue(String key) {
//		return null;
//	}

	public static void main(String[] args) throws Exception {
		HashMap map = new HashMap();
		map.put("key", "value");
		map.put("other-key", "other-value");
		ObjectMapper mapper = new ObjectMapper();
		String value = mapper.writeValueAsString(map);
		System.out.println(value);
		System.out.println(mapper.readValue(value, HashMap.class));
	}

}

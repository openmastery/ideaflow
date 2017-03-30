package org.openmastery.publisher.api.event;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.openmastery.publisher.api.AbstractPositionable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
public class Event extends AbstractPositionable {

	private String fullPath;
	private String description;
	private EventType type;

	@JsonIgnore
	private Long id;


}

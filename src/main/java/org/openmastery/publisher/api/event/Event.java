package org.openmastery.publisher.api.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.openmastery.publisher.api.AbstractPositionable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Event extends AbstractPositionable {

	private Long id;

	private String comment;
	private EventType type;

}

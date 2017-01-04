package org.openmastery.storyweb.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlossaryDefinition {

	private Long id;

	private String name;
	private String description;

}

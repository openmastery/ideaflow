package org.openmastery.publisher.api.journey;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormattableSnippet {
	String source;
	String contents;
}

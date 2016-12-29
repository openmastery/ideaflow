package org.openmastery.publisher.api.annotation;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQAnnotation {
	Long eventId;
	String faq;
}

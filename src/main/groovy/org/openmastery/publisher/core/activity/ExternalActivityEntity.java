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

@Entity(name = "activity")
@Data
@Builder
@EqualsAndHashCode(callSuper = true, of = {})
//@NoArgsConstructor
//@AllArgsConstructor
public class ExternalActivityEntity extends ActivityEntity {

//	private String comment;

}

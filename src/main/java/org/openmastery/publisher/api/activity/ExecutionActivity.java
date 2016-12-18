package org.openmastery.publisher.api.activity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ExecutionActivity extends AbstractActivity {

	private String processName;
	private String executionType;

	private boolean debug;
	private boolean failed;

}

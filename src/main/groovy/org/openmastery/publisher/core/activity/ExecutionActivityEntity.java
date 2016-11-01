package org.openmastery.publisher.core.activity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("execution")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExecutionActivityEntity extends ActivityEntity {

	private static final String PROCESS_NAME_KEY = "processName";
	private static final String EXIT_CODE_KEY = "exitCode";
	private static final String EXECUTION_TASK_TYPE = "executionTaskType";
	private static final String IS_DEBUG = "isDebug";

	private ExecutionActivityEntity() {
	}

	private ExecutionActivityEntity(long id, long ownerId, long taskId, LocalDateTime start, LocalDateTime end,
	                                String processName, int exitCode, String executionTaskType, boolean isDebug) {
		super(id, ownerId, taskId, start, end);
		setProcessName(processName);
		setExitCode(exitCode);
		setExecutionTaskType(executionTaskType);
		setDebug(isDebug);
	}

	public String getProcessName() {
		return getMetadataValue(PROCESS_NAME_KEY);
	}

	public void setProcessName(String processName) {
		setMetadataField(PROCESS_NAME_KEY, processName);
	}

	public int getExitCode() {
		return getMetadataValueAsInteger(EXIT_CODE_KEY);
	}

	public void setExitCode(int exitCode) {
		setMetadataField(EXIT_CODE_KEY, exitCode);
	}

	public String getExecutionTaskType() {
		return getMetadataValue(EXECUTION_TASK_TYPE);
	}

	public void setExecutionTaskType(String executionTaskType) {
		setMetadataField(EXECUTION_TASK_TYPE, executionTaskType);
	}

	public boolean isDebug() {
		return getMetadataValueAsBoolean(IS_DEBUG);
	}

	public void setDebug(boolean debug) {
		setMetadataField(IS_DEBUG, debug);
	}


	public static ExecutionActivityEntityBuilder builder() {
		return new ExecutionActivityEntityBuilder();
	}

	public static class ExecutionActivityEntityBuilder extends ActivityEntityBuilder<ExecutionActivityEntityBuilder> {

		private String processName;
		private int exitCode;
		private String executionTaskType;
		private boolean isDebug;

		public ExecutionActivityEntity build() {
			return new ExecutionActivityEntity(id, ownerId, taskId, start, end, processName, exitCode, executionTaskType, isDebug);
		}

		public ExecutionActivityEntityBuilder processName(String processName) {
			this.processName = processName;
			return this;
		}

		public ExecutionActivityEntityBuilder exitCode(int exitCode) {
			this.exitCode = exitCode;
			return this;
		}

		public ExecutionActivityEntityBuilder executionTaskType(String executionTaskType) {
			this.executionTaskType = executionTaskType;
			return this;
		}

		public ExecutionActivityEntityBuilder debug(boolean debug) {
			isDebug = debug;
			return this;
		}

	}

}

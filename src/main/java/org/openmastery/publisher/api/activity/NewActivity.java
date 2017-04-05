package org.openmastery.publisher.api.activity;


import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.batch.BatchItem;

public interface NewActivity extends BatchItem {

	Long getDurationInSeconds();

	LocalDateTime getEndTime();
}

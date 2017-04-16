package org.openmastery.publisher.api.activity;


import org.openmastery.publisher.api.batch.BatchItem;

import java.time.LocalDateTime;

public interface NewActivity extends BatchItem {

	Long getDurationInSeconds();

	LocalDateTime getEndTime();
}

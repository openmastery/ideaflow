package org.openmastery.publisher.api.activity;


import org.joda.time.LocalDateTime;

public interface NewActivity {

	Long getDurationInSeconds();

	LocalDateTime getEndTime();
}

package org.openmastery.time;

import java.time.LocalDateTime;

public interface TimeService {

	LocalDateTime now();

	org.joda.time.LocalDateTime jodaNow();

}

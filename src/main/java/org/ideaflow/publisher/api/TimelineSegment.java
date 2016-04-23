package org.ideaflow.publisher.api;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

public class TimelineSegment {

    private long id;

    private LocalDateTime start;
    private LocalDateTime end;

    private int relativeStart;
    private int relativeEnd;

    List<TimeBand> timeBands;
    List<TimeBandGroup> timeBandGroups;
}

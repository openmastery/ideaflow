package org.ideaflow.publisher.api;

import java.time.LocalDateTime;
import java.util.List;

public class TimeBandGroup {

    private long id;

    private LocalDateTime start;
    private LocalDateTime end;

    private int relativeStart;
    private int relativeEnd;

    List<TimeBand> linkedTimeBands;
}

//conflict <- rework | nested conflict | nested conflict | end rework

//group comment is first comment in the grouping.
//group contains conflict, rework with nested conflicts

//subtask in the middle of a timeband, need to split the band.
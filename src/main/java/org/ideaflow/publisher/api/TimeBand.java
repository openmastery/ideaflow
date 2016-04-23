package org.ideaflow.publisher.api;

import java.time.LocalDateTime;
import java.util.List;


class TimeBand {

    private long id;

    private LocalDateTime start;
    private LocalDateTime end;

    private int relativeStart;
    private int relativeEnd;

    private IdeaFlowStateType type;

    private List<TimeBand> nestedBands;
}



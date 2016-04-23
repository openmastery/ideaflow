package org.ideaflow.publisher.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeBandGroup {

    private long id;

    private List<TimeBand> linkedTimeBands;

    public void addLinkedTimeBand(TimeBand linkedTimeBand) {
        linkedTimeBands.add(linkedTimeBand);
    }

    public LocalDateTime getStart() {
        return linkedTimeBands.get(0).getStart();
    }

    public LocalDateTime getEnd() {
        return linkedTimeBands.get(linkedTimeBands.size() - 1).getEnd();
    }

    public Duration getDuration() {
        return TimeBand.sumDuration(linkedTimeBands);
    }

    public static Duration sumDuration(List<TimeBandGroup> timeBandGroups) {
        Duration duration = Duration.ZERO;
        for (TimeBandGroup timeBand : timeBandGroups) {
            duration = duration.plus(timeBand.getDuration());
        }
        return duration;
    }

}

//conflict <- rework | nested conflict | nested conflict | end rework

//group comment is first comment in the grouping.
//group contains conflict, rework with nested conflicts

//subtask in the middle of a timeband, need to split the band.
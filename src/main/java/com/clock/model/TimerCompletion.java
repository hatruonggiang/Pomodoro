package com.clock.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

public class TimerCompletion implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String timerName;
    private final Instant completionTime;
    private final long durationMillis;

    public TimerCompletion(String timerName, Instant completionTime, long durationMillis) {
        this.timerName = timerName;
        this.completionTime = completionTime;
        this.durationMillis = durationMillis;
    }

    public String getTimerName() {
        return timerName;
    }

    public Instant getCompletionTime() {
        return completionTime;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public Duration getDuration() {
        return Duration.ofMillis(durationMillis);
    }

    @Override
    public String toString() {
        return String.format("%s completed at %s (Duration: %d ms)",
                timerName, completionTime, durationMillis);
    }
}

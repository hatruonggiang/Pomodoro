package com.clock.model;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CountdownTimer {
    private String name;
    private Duration targetDuration;
    private Duration remainingTime;
    private Instant startTime;
    private boolean isRunning;
    private boolean isCompleted;
    private List<TimerCompletion> completionHistory;

    public CountdownTimer(String name, Duration targetDuration) {
        this.name = name;
        this.targetDuration = targetDuration;
        this.remainingTime = targetDuration;
        this.isRunning = false;
        this.isCompleted = false;
        this.completionHistory = new ArrayList<>();
    }

    public void start() {
        if (!isRunning && !isCompleted) {
            startTime = Instant.now();
            isRunning = true;
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            updateRemainingTime();
        }
    }

    public void reset() {
        isRunning = false;
        isCompleted = false;
        remainingTime = targetDuration;
        startTime = null;
    }

    public Duration getRemainingTime() {
        if (isRunning) {
            updateRemainingTime();
        }
        return remainingTime;
    }

    private void updateRemainingTime() {
        if (isRunning) {
            Duration elapsed = Duration.between(startTime, Instant.now());
            remainingTime = targetDuration.minus(elapsed);
            
            if (remainingTime.isNegative() || remainingTime.isZero()) {
                remainingTime = Duration.ZERO;
                isRunning = false;
                isCompleted = true;
                // Record completion
                completionHistory.add(new TimerCompletion(
                    name,
                    Instant.now(),
                    targetDuration.toMillis()
                ));
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public Duration getTargetDuration() {
        return targetDuration;
    }

    public void setTargetDuration(Duration targetDuration) {
        this.targetDuration = targetDuration;
        if (!isRunning) {
            this.remainingTime = targetDuration;
        }
    }

    public List<TimerCompletion> getCompletionHistory() {
        return new ArrayList<>(completionHistory);
    }

    public void setCompletionHistory(List<TimerCompletion> history) {
        this.completionHistory = new ArrayList<>(history);
    }
} 
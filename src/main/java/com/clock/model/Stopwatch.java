package com.clock.model;

import java.time.Duration;
import java.time.Instant;

public class Stopwatch {
    private String name;
    private Instant startTime;
    private Instant stopTime;
    private boolean isRunning;
    private Duration totalTime;

    public Stopwatch(String name) {
        this.name = name;
        this.isRunning = false;
        this.totalTime = Duration.ZERO;
    }

    public void start() {
        if (!isRunning) {
            startTime = Instant.now();
            isRunning = true;
        }
    }

    public void stop() {
        if (isRunning) {
            stopTime = Instant.now();
            isRunning = false;
            totalTime = totalTime.plus(Duration.between(startTime, stopTime));
        }
    }

    public void reset() {
        isRunning = false;
        totalTime = Duration.ZERO;
        startTime = null;
        stopTime = null;
    }

    public Duration getCurrentTime() {
        if (isRunning) {
            return totalTime.plus(Duration.between(startTime, Instant.now()));
        }
        return totalTime;
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
} 
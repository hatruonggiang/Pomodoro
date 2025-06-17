package com.clock.controller;

import com.clock.model.Stopwatch;
import java.util.ArrayList;
import java.util.List;

public class StopwatchController {
    private List<Stopwatch> stopwatches;

    public StopwatchController() {
        this.stopwatches = new ArrayList<>();
    }

    public Stopwatch createStopwatch(String name) {
        Stopwatch stopwatch = new Stopwatch(name);
        stopwatches.add(stopwatch);
        return stopwatch;
    }

    public void startStopwatch(Stopwatch stopwatch) {
        stopwatch.start();
    }

    public void stopStopwatch(Stopwatch stopwatch) {
        stopwatch.stop();
    }

    public void resetStopwatch(Stopwatch stopwatch) {
        stopwatch.reset();
    }

    public List<Stopwatch> getAllStopwatches() {
        return new ArrayList<>(stopwatches);
    }

    public void removeStopwatch(Stopwatch stopwatch) {
        stopwatches.remove(stopwatch);
    }
} 
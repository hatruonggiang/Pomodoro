package com.clock.controller;

import com.clock.model.CountdownTimer;
import com.clock.util.TimerDataManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CountdownController {
    private List<CountdownTimer> timers;

    public CountdownController() {
        this.timers = TimerDataManager.loadTimers();
    }

    public CountdownTimer createTimer(String name, Duration duration) {
        CountdownTimer timer = new CountdownTimer(name, duration);
        timers.add(timer);
        saveTimers();
        return timer;
    }

    public void startTimer(CountdownTimer timer) {
        timer.start();
        saveTimers();
    }

    public void stopTimer(CountdownTimer timer) {
        timer.stop();
        saveTimers();
    }

    public void resetTimer(CountdownTimer timer) {
        timer.reset();
        saveTimers();
    }

    public List<CountdownTimer> getAllTimers() {
        return new ArrayList<>(timers);
    }

    public void removeTimer(CountdownTimer timer) {
        timers.remove(timer);
        saveTimers();
    }

    public void saveTimers() {
        TimerDataManager.saveTimers(timers);
    }
} 
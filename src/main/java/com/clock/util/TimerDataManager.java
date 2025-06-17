package com.clock.util;

import com.clock.model.CountdownTimer;
import com.clock.model.TimerCompletion;
import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TimerDataManager {
    private static final String DATA_FILE = "timers.dat";

    public static void saveTimers(List<CountdownTimer> timers) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            List<TimerData> timerDataList = new ArrayList<>();
            for (CountdownTimer timer : timers) {
                timerDataList.add(new TimerData(
                    timer.getName(),
                    timer.getTargetDuration().toMillis(),
                    timer.getRemainingTime().toMillis(),
                    timer.isRunning(),
                    timer.isCompleted(),
                    timer.getCompletionHistory()
                ));
            }
            oos.writeObject(timerDataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<CountdownTimer> loadTimers() {
        List<CountdownTimer> timers = new ArrayList<>();
        File file = new File(DATA_FILE);
        
        if (!file.exists()) {
            return timers;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            List<TimerData> timerDataList = (List<TimerData>) ois.readObject();
            
            for (TimerData data : timerDataList) {
                CountdownTimer timer = new CountdownTimer(data.name, Duration.ofMillis(data.targetDuration));
                if (data.isRunning) {
                    timer.start();
                }
                if (data.isCompleted) {
                    timer.stop();
                }
                if (data.completionHistory != null) {
                    timer.setCompletionHistory(data.completionHistory);
                }
                timers.add(timer);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return timers;
    }

    private static class TimerData implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name;
        private final long targetDuration;
        private final long remainingTime;
        private final boolean isRunning;
        private final boolean isCompleted;
        private final List<TimerCompletion> completionHistory;

        public TimerData(String name, long targetDuration, long remainingTime, 
                        boolean isRunning, boolean isCompleted, List<TimerCompletion> completionHistory) {
            this.name = name;
            this.targetDuration = targetDuration;
            this.remainingTime = remainingTime;
            this.isRunning = isRunning;
            this.isCompleted = isCompleted;
            this.completionHistory = completionHistory;
        }
    }
} 
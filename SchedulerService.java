package com.lifysyn.service;

import com.lifysyn.model.Task;
import com.lifysyn.util.CSVHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * SchedulerService: Core business logic for recommending task times.
 *
 * Logic:
 * 1. Add 30-min buffer before and after occupied time.
 * 2. For each task, check best_time → alternate_time_1 → alternate_time_2.
 * 3. First slot that doesn't clash = final_recommended_time.
 * 4. If all clash → "No suitable slot available".
 */
public class SchedulerService {

    /**
     * Convert "HH:MM" string to total minutes since midnight
     */
    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    /**
     * Add minutes to "HH:MM" string
     */
    private String addMinutes(String time, int minutes) {
        int total = timeToMinutes(time) + minutes;
        // Handle going past midnight
        total = (total + 1440) % 1440;
        int hours = total / 60;
        int mins = total % 60;
        return String.format("%02d:%02d", hours, mins);
    }

    /**
     * Calculate buffer start (occupied start - 30 minutes)
     */
    public String calculateBufferStart(String occupiedStart) {
        return addMinutes(occupiedStart, -30);
    }

    /**
     * Calculate buffer end (occupied end + 30 minutes)
     */
    public String calculateBufferEnd(String occupiedEnd) {
        return addMinutes(occupiedEnd, 30);
    }

    /**
     * Check if a time range (like "06:00-07:00") clashes with occupied/buffer window.
     *
     * Handles overnight ranges (e.g., "22:30-06:00").
     *
     * Returns true if there IS a clash, false if the slot is FREE.
     */
    public boolean clashesWithOccupied(String timeRange, String bufferStart, String bufferEnd) {
        if (timeRange == null || timeRange.equals("N/A")) return false;

        String[] parts = timeRange.split("-");
        if (parts.length != 2) return false;

        int slotStart = timeToMinutes(parts[0].trim());
        int slotEnd = timeToMinutes(parts[1].trim());
        int occStart = timeToMinutes(bufferStart);
        int occEnd = timeToMinutes(bufferEnd);

        // Handle overnight slot (e.g., sleep: 22:30-06:00 means slotEnd < slotStart)
        boolean slotOvernight = slotEnd < slotStart;
        boolean occOvernight = occEnd < occStart;

        if (!slotOvernight && !occOvernight) {
            // Both normal ranges — simple overlap check
            return slotStart < occEnd && slotEnd > occStart;
        } else if (slotOvernight && !occOvernight) {
            // Slot is overnight
            return (slotStart < occEnd) || (slotEnd > occStart);
        } else if (!slotOvernight && occOvernight) {
            // Occupied is overnight
            return (occStart < slotEnd) || (occEnd > slotStart);
        } else {
            // Both overnight — always some overlap
            return true;
        }
    }

    /**
     * Generate the full plan for a list of selected task names.
     *
     * @param selectedTasks List of task names chosen by user
     * @param occupiedStart User's occupied start time (HH:MM)
     * @param occupiedEnd   User's occupied end time (HH:MM)
     * @return List of Task objects with finalRecommendedTime set
     */
    public List<Task> generatePlan(List<String> selectedTasks,
                                   String occupiedStart, String occupiedEnd) {

        String bufferStart = calculateBufferStart(occupiedStart);
        String bufferEnd = calculateBufferEnd(occupiedEnd);

        List<Task> result = new ArrayList<>();

        for (String taskName : selectedTasks) {
            Task task = CSVHandler.getTaskByName(taskName);

            if (task == null) {
                // Task not found in CSV — create placeholder
                Task placeholder = new Task();
                placeholder.setTaskName(taskName);
                placeholder.setBestTime("N/A");
                placeholder.setAlternateTime1("N/A");
                placeholder.setAlternateTime2("N/A");
                placeholder.setAvoidTime("N/A");
                placeholder.setDuration(30);
                placeholder.setReason("No data available");
                placeholder.setFinalRecommendedTime("No suitable slot available");
                result.add(placeholder);
                continue;
            }

            // Determine final recommended time
            String finalTime;

            if (!clashesWithOccupied(task.getBestTime(), bufferStart, bufferEnd)) {
                finalTime = task.getBestTime();
            } else if (!clashesWithOccupied(task.getAlternateTime1(), bufferStart, bufferEnd)) {
                finalTime = task.getAlternateTime1();
            } else if (!clashesWithOccupied(task.getAlternateTime2(), bufferStart, bufferEnd)) {
                finalTime = task.getAlternateTime2();
            } else {
                finalTime = "No suitable slot available";
            }

            task.setFinalRecommendedTime(finalTime);
            result.add(task);
        }

        return result;
    }

    /**
     * Return buffer start and end as a formatted string for display
     */
    public String getBufferInfo(String occupiedStart, String occupiedEnd) {
        return calculateBufferStart(occupiedStart) + " – " + calculateBufferEnd(occupiedEnd);
    }
}
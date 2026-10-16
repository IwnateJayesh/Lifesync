package com.lifysyn.model;

public class Task {
    private String taskName;
    private String bestTime;
    private String alternateTime1;
    private String alternateTime2;
    private String avoidTime;
    private int duration; // in minutes
    private String reason;
    private String finalRecommendedTime;

    public Task() {}

    public Task(String taskName, String bestTime, String alternateTime1,
                String alternateTime2, String avoidTime, int duration, String reason) {
        this.taskName = taskName;
        this.bestTime = bestTime;
        this.alternateTime1 = alternateTime1;
        this.alternateTime2 = alternateTime2;
        this.avoidTime = avoidTime;
        this.duration = duration;
        this.reason = reason;
    }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getBestTime() { return bestTime; }
    public void setBestTime(String bestTime) { this.bestTime = bestTime; }

    public String getAlternateTime1() { return alternateTime1; }
    public void setAlternateTime1(String alternateTime1) { this.alternateTime1 = alternateTime1; }

    public String getAlternateTime2() { return alternateTime2; }
    public void setAlternateTime2(String alternateTime2) { this.alternateTime2 = alternateTime2; }

    public String getAvoidTime() { return avoidTime; }
    public void setAvoidTime(String avoidTime) { this.avoidTime = avoidTime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getFinalRecommendedTime() { return finalRecommendedTime; }
    public void setFinalRecommendedTime(String finalRecommendedTime) {
        this.finalRecommendedTime = finalRecommendedTime;
    }
}
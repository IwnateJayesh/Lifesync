package com.lifysyn.model;

public class Schedule {
    private int userId;
    private String date;
    private String occupationType;
    private String occupiedStart;
    private String occupiedEnd;
    private String bufferStart;
    private String bufferEnd;

    public Schedule() {}

    public Schedule(int userId, String date, String occupationType,
                    String occupiedStart, String occupiedEnd,
                    String bufferStart, String bufferEnd) {
        this.userId = userId;
        this.date = date;
        this.occupationType = occupationType;
        this.occupiedStart = occupiedStart;
        this.occupiedEnd = occupiedEnd;
        this.bufferStart = bufferStart;
        this.bufferEnd = bufferEnd;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getOccupationType() { return occupationType; }
    public void setOccupationType(String occupationType) { this.occupationType = occupationType; }

    public String getOccupiedStart() { return occupiedStart; }
    public void setOccupiedStart(String occupiedStart) { this.occupiedStart = occupiedStart; }

    public String getOccupiedEnd() { return occupiedEnd; }
    public void setOccupiedEnd(String occupiedEnd) { this.occupiedEnd = occupiedEnd; }

    public String getBufferStart() { return bufferStart; }
    public void setBufferStart(String bufferStart) { this.bufferStart = bufferStart; }

    public String getBufferEnd() { return bufferEnd; }
    public void setBufferEnd(String bufferEnd) { this.bufferEnd = bufferEnd; }
}
package com.studyHard.teamnova_android;

public class Timer_Data {
    long start_time;
    long end_time;
    String rate;
    String date;
    String studyTime;

    public Timer_Data(String date, long start_time, long end_time, String studyTime, String rate) {
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.rate = rate;
        this.studyTime = studyTime;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(String studyTime) {
        this.studyTime = studyTime;
    }
}

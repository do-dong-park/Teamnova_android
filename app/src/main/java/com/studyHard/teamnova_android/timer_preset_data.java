package com.studyHard.teamnova_android;

public class timer_preset_data {
    private String date;
    private String title;
    private String timeSet;

    public timer_preset_data(String date, String title, String timeSet) {
        this.date = date;
        this.title = title;
        this.timeSet = timeSet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeSet() {
        return timeSet;
    }

    public void setTimeSet(String timeSet) {
        this.timeSet = timeSet;
    }
}

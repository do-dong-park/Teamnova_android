package com.studyHard.teamnova_android;

public class Memo_PHOTO_Data {
    private String date;
    private String memo_content;
    private int ViewType;

    public Memo_PHOTO_Data(String date, String memo_content, int viewType) {
        this.date = date;
        this.memo_content = memo_content;
        this.ViewType = viewType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMemo_content() {
        return memo_content;
    }

    public void setMemo_content(String memo_content) {
        this.memo_content = memo_content;
    }

    public int getViewType() {
        return ViewType;
    }

    public void setViewType(int viewType) {
        ViewType = viewType;
    }
}

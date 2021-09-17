package com.studyHard.teamnova_android;

import android.net.Uri;

public class Memo_Data {
    private String memo_title;
    private String date;
    private String memo_content;
    private Uri image;

    public Memo_Data(String date, String memo_title, String memo_content, Uri image) {
        this.date = date;
        this.memo_title = memo_title;
        this.memo_content = memo_content;
        this.image = image;
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

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getMemo_title() {
        return memo_title;
    }

    public void setMemo_title(String memo_title) {
        this.memo_title = memo_title;
    }

}

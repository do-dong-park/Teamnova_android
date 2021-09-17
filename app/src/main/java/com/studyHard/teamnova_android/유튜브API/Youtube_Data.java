package com.studyHard.teamnova_android.유튜브API;

public class Youtube_Data {
    String videoId;
    String title;
    String channelTitle;
    String viewCount;
    String publishedAt;


    public Youtube_Data(String videoId, String title,String channelTitle, String viewCount,
                        String publishedAt) {
        super();
        this.videoId = videoId;
        this.title = title;
        this.channelTitle = channelTitle;
        this.viewCount = viewCount;
        this.publishedAt = publishedAt;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}


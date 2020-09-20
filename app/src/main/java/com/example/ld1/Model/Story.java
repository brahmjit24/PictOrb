package com.example.ld1.Model;

public class Story {
    private String imageurl;
    private long storystart;
    private long storyend;
    private String storyid;
    private String userid;

    public Story() {
    }

    public Story(String imageurl, long storystart, long storyend, String storyid, String userid) {
        this.imageurl = imageurl;
        this.storystart = storystart;
        this.storyend = storyend;
        this.storyid = storyid;
        this.userid = userid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public long getStorystart() {
        return storystart;
    }

    public void setStorystart(long storystart) {
        this.storystart = storystart;
    }

    public long getStoryend() {
        return storyend;
    }

    public void setStoryend(long storyend) {
        this.storyend = storyend;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}

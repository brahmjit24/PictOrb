package com.example.ld1.Model;

import java.util.List;

public class Post {

    String id;
    String imageurl;
    String description;
    String location;
    List<String> tags;
    List<String> usermentioned;
    String publisher;
    boolean isglobal;
    boolean iscommentable;
    boolean isdownloadable;
    String when;

    public Post() {
    }

    public Post(String id, String imageurl, String description, String location, List<String> tags, List<String> usermentioned, String publisher, boolean isglobal, boolean iscommentable, boolean isdownloadable, String when) {
        this.id = id;
        this.imageurl = imageurl;
        this.description = description;
        this.location = location;
        this.tags = tags;
        this.usermentioned = usermentioned;
        this.publisher = publisher;
        this.isglobal = isglobal;
        this.iscommentable = iscommentable;
        this.isdownloadable = isdownloadable;
        this.when = when;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getUsermentioned() {
        return usermentioned;
    }

    public void setUsermentioned(List<String> usermentioned) {
        this.usermentioned = usermentioned;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public boolean isIsglobal() {
        return isglobal;
    }

    public void setIsglobal(boolean isglobal) {
        this.isglobal = isglobal;
    }

    public boolean isIscommentable() {
        return iscommentable;
    }

    public void setIscommentable(boolean iscommentable) {
        this.iscommentable = iscommentable;
    }

    public boolean isIsdownloadable() {
        return isdownloadable;
    }

    public void setIsdownloadable(boolean isdownloadable) {
        this.isdownloadable = isdownloadable;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }
}




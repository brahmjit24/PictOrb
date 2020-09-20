package com.example.ld1.Model;

public class Comment {
    private String comment;
    private String publisher;
    private String when;

    public Comment() {
    }

    public Comment(String comment, String publisher, String when) {
        this.comment = comment;
        this.publisher = publisher;
        this.when = when;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }
}

package com.example.ld1.Model;

public class Tag {
    private String name;
    private String by;
    private String when;

    public Tag() {
    }

    public Tag(String name, String by, String when) {
        this.name = name;
        this.by = by;
        this.when = when;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }
}

package com.frankmoley.lil.data.entity;

public class NoteDraft {
    private String title;
    private String content;

    public NoteDraft() {}

    public NoteDraft(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "NoteDraft [title=" + title + ", content=" + content + "]";
    }
}

package com.devoxx.watson.model;

import static java.lang.Math.abs;
import static java.lang.String.*;

/**
 * @author Stephan Janssen
 */
public class DocumentContent {

    private String title;

    private String author;

    private String thumbnail;

    private String content;

    private String link;

    public String getId() {
        return valueOf(abs(title.hashCode()));
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }
}

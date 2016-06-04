package com.devoxx.watson.model;

import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.String.valueOf;

/**
 * @author Stephan Janssen
 */
public class AlchemyContent {

    private String title;

    private String link;

    private String content;

    private String thumbnail;

    private String publicationDate;

    private String authors;

    private String language;

    private String sentiment;

    private JsonObject emotions;

    public String getId() {
        return valueOf(abs(title.hashCode()));
    }

    public String getPublicationDate() {
        SimpleDateFormat fromUser = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat myFormat = new SimpleDateFormat("MMMMM d ''yy");

        try {
            return myFormat.format(fromUser.parse(publicationDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPublicationDate(final String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(final String authors) {
        this.authors = authors;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(final String sentimentScore) {
        this.sentiment = sentimentScore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getEmotions() {
        final Map<String, Double> map = new HashMap<>();
        map.put("anger", emotions.get("anger").getAsDouble());
        map.put("disgust", emotions.get("disgust").getAsDouble());
        map.put("fear", emotions.get("fear").getAsDouble());
        map.put("joy", emotions.get("joy").getAsDouble());
        map.put("sadness", emotions.get("sadness").getAsDouble());

        Map.Entry<String, Double> maxEntry = null;

        for (Map.Entry<String, Double> entry : map.entrySet())  {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        if (maxEntry != null) {
            return maxEntry.getKey() + " (" + maxEntry.getValue() + ")";
        } else {
            return "undefined";
        }
    }

    public void setEmotions(final JsonObject emotions) {
        this.emotions = emotions;
    }
}

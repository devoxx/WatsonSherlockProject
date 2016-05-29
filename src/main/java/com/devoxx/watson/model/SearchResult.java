package com.devoxx.watson.model;

/**
 * @author Stephan Janssen
 */
public class SearchResult {

    private String id;

    private String label;

    private Double score;

    public SearchResult(String id, String label, Double score) {
        this.id = id;
        this.label = label;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(final Double score) {
        this.score = score;
    }
}

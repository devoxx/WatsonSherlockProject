package com.devoxx.watson.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by danieldeluca on 30/07/16.
 * Robot Concept Search Result Class
 */
public class DocumentSearchContent extends DocumentContent {
    private Double searchScore;
    private String publicationDate;


    public Double getSearchScore() {
        return searchScore;
    }

    public void setSearchScore(Double searchScore) {
        this.searchScore = searchScore;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof DocumentSearchContent)) return false;

        DocumentSearchContent that = (DocumentSearchContent) o;

        return new EqualsBuilder()
                .append(getSearchScore(), that.getSearchScore())
                .append(getPublicationDate(), that.getPublicationDate())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getSearchScore())
                .append(getPublicationDate())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("searchScore", searchScore)
                .append("publicationDate", publicationDate)
                .toString();
    }
}

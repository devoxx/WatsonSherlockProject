package com.devoxx.watson.model;

import java.util.List;

/**
 * @author Stephan Janssen
 */
public class ConversationModel {

    private String conversationId;
    private String clientId;
    private String input;
    private String watsonResponse;
    private List<Article> articles;
    private Integer totalPages;
    private Integer numArticles;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(final String conversationId) {
        this.conversationId = conversationId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(final String input) {
        this.input = input;
    }

    public String getWatsonResponse() {
        return watsonResponse;
    }

    public void setWatsonResponse(final String watsonResponse) {
        this.watsonResponse = watsonResponse;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(final List<Article> articles) {
        this.articles = articles;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(final Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getNumArticles() {
        return numArticles;
    }

    public void setNumArticles(final Integer numArticles) {
        this.numArticles = numArticles;
    }
}

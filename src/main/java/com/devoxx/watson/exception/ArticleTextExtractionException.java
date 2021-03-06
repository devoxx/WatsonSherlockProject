package com.devoxx.watson.exception;

/**
 * @author Stephan Janssen
 */
public class ArticleTextExtractionException extends Throwable {

    public ArticleTextExtractionException() {
    }

    public ArticleTextExtractionException(final String message) {
        super(message);
    }

    public ArticleTextExtractionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}

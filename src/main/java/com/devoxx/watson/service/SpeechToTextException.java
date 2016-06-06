package com.devoxx.watson.service;

/**
 * @author Stephan Janssen
 */
public class SpeechToTextException extends Throwable {

    SpeechToTextException() {
    }

    SpeechToTextException(final String message) {
        super(message);
    }

    public SpeechToTextException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

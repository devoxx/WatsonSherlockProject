package com.devoxx.watson.exception;

/**
 * @author Stephan Janssen
 */
public class SpeechToTextException extends Throwable {

    public SpeechToTextException() {
    }

    public SpeechToTextException(final String message) {
        super(message);
    }

    public SpeechToTextException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

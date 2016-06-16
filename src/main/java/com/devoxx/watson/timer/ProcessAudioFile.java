package com.devoxx.watson.timer;

import com.devoxx.watson.controller.WatsonController;
import com.devoxx.watson.exception.SpeechToTextException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Stephan Janssen
 */
@Component
class ProcessAudioFile {

    private static final Logger LOGGER = Logger.getLogger(ProcessAudioFile.class.getName());

    @Autowired
    private WatsonController watsonController;

    /**
     * Process the audio file.
     *
     * @param audioFile the audio file
     * @param docName   the document name
     * @param link      the YouTube link
     * @param speakers  the speaker(s)
     */
    void execute(final File audioFile,
                 final String docName,
                 final String link,
                 final String speakers) {
        try {
            String transcript = watsonController.processSpeechToText(audioFile, docName);

            watsonController.createAudioDocument(docName, link, transcript, speakers);

            if (audioFile.delete()) {
                LOGGER.log(Level.INFO, "File {0} removed", audioFile.getName());
            }

        } catch (SpeechToTextException e) {
            LOGGER.severe(e.toString());
        }
    }
}

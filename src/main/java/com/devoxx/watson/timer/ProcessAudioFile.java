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

    private WatsonController watsonController;

    @Autowired
    public void setWatsonController(final WatsonController watsonController) {
        this.watsonController = watsonController;
    }

    /**
     * Process the audio file.
     *
     * @param audioFile the audio file
     * @param docName   the document name
     * @param link      the YouTube link
     * @param speakers  the speaker(s)
     * @param audioAbstract      the abstract text
     * @param audioModel the model used for recognizing the audio
     */
    void execute(final File audioFile,
                 final String docName,
                 final String link,
                 final String speakers,
                 final String audioAbstract,
                 final String audioModel) {

        //LOGGER.log(Level.INFO, "docName: {0}", docName);
        //LOGGER.log(Level.INFO, "link: {0}", link);
        //LOGGER.log(Level.INFO, "speakers: {0}", speakers);
        //LOGGER.log(Level.INFO, "audioAbstract: {0}", audioAbstract);

        try {
            String transcript = watsonController.processSpeechToText(audioFile, docName, audioAbstract, audioModel);

            watsonController.createAudioDocument(docName, link, transcript, speakers);

            if (audioFile.delete()) {
                LOGGER.log(Level.INFO, "File {0} removed", audioFile.getName());
            }

        } catch (SpeechToTextException e) {
            LOGGER.severe(e.toString());
        }
    }
}

package com.devoxx.watson.controller;

import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
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
    private ConceptInsightsService conceptInsightsService;

    @Autowired
    private SpeechToText speechToText;

    private String docName;

    /**
     * Process the audio file asynchronously.
     *
     * @param audioFile     the audio file
     */
    void execute(final File audioFile,
                 final String docName,
                 final String link) {

        this.docName = docName;

        // Start speech to text process
        SpeechResults speechResults = processSpeechToText(audioFile);

        LOGGER.info("Add speech results into document");
        StringBuilder stringBuilder = new StringBuilder();
        for (Transcript transcript : speechResults.getResults()) {
            stringBuilder.append(transcript.getAlternatives().get(0).getTranscript());
        }

        conceptInsightsService.createDocument(docName, link, stringBuilder.toString());

        if (audioFile.delete()) {
            LOGGER.log(Level.INFO, "File {0} removed", audioFile.getName());
        }
    }

    /**
     * Start speech to text process.
     *
     * @param audioFile the audio file
     */
    private SpeechResults processSpeechToText(final File audioFile) {
        LOGGER.log(Level.INFO, "process speech to text service for {0}", docName);

        final RecognizeOptions options = new RecognizeOptions
                .Builder()
                .contentType("audio/ogg")
                .continuous(true)
                .interimResults(false)
                .build();

        SpeechResults speechResults = null;

        // get speech results
        LOGGER.log(Level.INFO, "get speech results for {0}", docName);
        try {
            speechResults = speechToText.recognize(audioFile, options).execute();

            if (speechResults != null) {
                LOGGER.log(Level.INFO, "got speech results (index={0})", speechResults.getResultIndex());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getCause().toString());
        }

        return speechResults;
    }
}

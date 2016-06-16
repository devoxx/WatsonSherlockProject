package com.devoxx.watson.service;

import com.devoxx.watson.exception.SpeechToTextException;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
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
public class SpeechToTextService {

    private static final Logger LOGGER = Logger.getLogger(SpeechToTextService.class.getName());

    private SpeechToText speechToText;

    private final String keywords[] = { "modules", "module" };

    @Autowired
    public void setSpeechToText(final SpeechToText speechToText) {
        this.speechToText = speechToText;
    }

    /**
     * Start speech to text processAudioFile.
     *
     * @param audioFile the audio file
     * @param docName the document name
     * @return the transcript
     */
    public String processAudioFile(final File audioFile, final String docName) throws SpeechToTextException {

        LOGGER.log(Level.INFO, "processAudioFile speech to text service for {0}", docName);

// TODO - Try this
//        curl -X POST -u <username>:<password>
//                --header "Content-Type: audio/flac"
//                --header "Transfer-Encoding: chunked"
//                --data-binary @<path>0001.flac
//        "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true&timestamps=true&max_alternatives=3"

        final RecognizeOptions options = new RecognizeOptions
                .Builder()
                .contentType("audio/ogg")
                .continuous(true)
                .wordConfidence(true)
                .interimResults(false)
                .build();

        // get speech results
        LOGGER.log(Level.INFO, "get speech results for {0}", docName);

        SpeechResults speechResults = speechToText.recognize(audioFile, options).execute();

        if (speechResults == null) {
            throw new SpeechToTextException("Something went wrong with processing audio file");
        }

        LOGGER.info("Add speech results into document");
        StringBuilder stringBuilder = new StringBuilder();
        for (Transcript transcript : speechResults.getResults()) {
            stringBuilder.append(transcript.getAlternatives().get(0).getTranscript());
        }

        return stringBuilder.toString();
    }
}

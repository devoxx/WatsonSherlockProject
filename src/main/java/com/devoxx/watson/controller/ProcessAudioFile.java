package com.devoxx.watson.controller;

import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Corpus;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Document;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Part;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Stephan Janssen
 */
@Component
class ProcessAudioFile {

    private static final Logger LOGGER = Logger.getLogger(ProcessAudioFile.class.getName());

    @Autowired
    private ConceptInsights conceptInsights;

    @Autowired
    private SpeechToText speechToText;

    @Autowired
    private Corpus corpus;

    private String docName;

    private String youTubeLink;

    /**
     * Process the audio file asynchronously.
     *
     * @param audioFile     the audio file
     */
    void execute(final File audioFile,
                 final String docName,
                 final String youTubeLink) {

        this.docName = docName;

        this.youTubeLink = youTubeLink;

        // Start speech to text process
        SpeechResults speechResults = processSpeechToText(audioFile);

        // initialize concept insights service
        processConceptInsights(speechResults);

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
        LOGGER.info("process speech to text service");

        final RecognizeOptions options = new RecognizeOptions();
        options.contentType("audio/ogg");
        options.continuous(true);
        options.interimResults(false);

        SpeechResults speechResults = null;

        // get speech results
        LOGGER.info("get speech results");
        try {
            speechResults = speechToText.recognize(audioFile, options);
            if (speechResults != null) {
                LOGGER.log(Level.INFO, "got speech results (index={0})", speechResults.getResultIndex());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getCause().toString());
        }

        return speechResults;
    }

    /**
     * Concept insights service.
     */
    private void processConceptInsights(final SpeechResults speechResults) {

        LOGGER.info("create corpus");
        final String account = conceptInsights.getFirstAccountId();

        LOGGER.info("create document");
        Document newDocument = new Document(corpus, UUID.randomUUID().toString());
        newDocument.setName(docName);
        newDocument.setLabel(docName);

        final Map<String, String> userFields = new HashMap<>();
        userFields.put("youTubeLink", youTubeLink);
        newDocument.setUserFields(userFields);

        LOGGER.info("Add speech results into document");
        StringBuilder stringBuilder = new StringBuilder();
        for (Transcript transcript : speechResults.getResults()) {
            stringBuilder.append(transcript.getAlternatives().get(0).getTranscript());
        }

        newDocument.addParts(new Part("part_", stringBuilder.toString(), HttpMediaType.TEXT_PLAIN));

        conceptInsights.createDocument(newDocument);

        // Why is the follow step needed?  GET / UPDATE

        LOGGER.info("get document");
        Document foundDocument = conceptInsights.getDocument(newDocument);

        LOGGER.info("update document");
        conceptInsights.updateDocument(foundDocument );
    }
}
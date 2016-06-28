package com.devoxx.watson.service;

import com.devoxx.watson.exception.SpeechToTextException;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Watson Speech to Text (STT) service.
 *
 * @author Stephan Janssen
 * @author James Weaver
 */
@Component
public class SpeechToTextService {

    private static final Logger LOGGER = Logger.getLogger(SpeechToTextService.class.getName());

    private AlchemyLanguageService alchemyLanguageService;

    private ConceptInsightsService conceptInsightsService;

    private SpeechToText speechToText;

    @Autowired
    public void setSpeechToText(final SpeechToText speechToText) {
        this.speechToText = speechToText;
    }

    @Autowired
    public void setAlchemyLanguageService(final AlchemyLanguageService alchemyLanguageService) {
        this.alchemyLanguageService = alchemyLanguageService;
    }

    @Autowired
    public void setConceptInsightsService(final ConceptInsightsService conceptInsightsService) {
        this.conceptInsightsService = conceptInsightsService;
    }

    /**
     * Start speech to text processAudioFile.  Uses a supplied array of keywords to spot in the recognized text.
     *
     * @param audioFile the audio file
     * @param docName the document name
     * @param abstractText the presentation abstract text
     * @param audioModel the model name used for the audio recognition.
     * @return the transcript
     */
    public String processAudioFile(final File audioFile,
                                   final String docName,
                                   final String abstractText,
                                   final String audioModel) throws SpeechToTextException {

        LOGGER.log(Level.INFO, "processAudioFile speech to text service for {0}", docName);

        final String[] keywords = obtainKeywordsFromAbstractText(abstractText);

        final boolean keywordsSupplied = keywords.length > 0;

        final RecognizeOptions.Builder optionsBuilder = new RecognizeOptions.Builder()
            .contentType("audio/ogg")
            .continuous(true)
            .timestamps(true)
            .model(audioModel)
            .maxAlternatives(3)
            .interimResults(false);

        if (keywordsSupplied) {
            optionsBuilder.keywords(keywords)
                          .keywordsThreshold(0.001);  //TODO: Make this configurable
        }

        final RecognizeOptions options = optionsBuilder.build();

        // get speech results
        LOGGER.log(Level.INFO, "get speech results for {0}", docName);

        SpeechResults speechResults = speechToText.recognize(audioFile, options).execute();

        if (speechResults == null) {
            throw new SpeechToTextException("Something went wrong with processing audio file");
        }
        else {
            LOGGER.log(Level.INFO, "speechResults:\n" + speechResults.toString());
        }

        LOGGER.info("Add speech results into document");
        StringBuilder stringBuilder = new StringBuilder();
        for (Transcript transcript : speechResults.getResults()) {
            SpeechAlternative speechAlternative = transcript.getAlternatives().get(0);
            Map<String, List<KeywordsResult>> keywordsResultMap = transcript.getKeywordsResult();
            String phrase = "";

            for (SpeechTimestamp speechTimestamp : speechAlternative.getTimestamps()) {
                String wordToConcatenate = speechTimestamp.getWord();
                if (keywordsSupplied) {
                    // If any keywords are spotted, check each of the words in the first (primary) alternative phrase for a
                    // spotted keyword with the same start/end timestamps, and concatenate the words.
                    String spottedKeyword = obtainSpottedKeywordByTimestamp(keywordsResultMap, speechTimestamp);

                    if (spottedKeyword.length() > 0) {
                        LOGGER.info("spottedKeyword: " + spottedKeyword);
                        wordToConcatenate = spottedKeyword;
                    }
                }
                phrase += wordToConcatenate + " ";
            }
            stringBuilder.append(phrase).append("\n");
        }

        LOGGER.log(Level.INFO, "transcript: \n" + stringBuilder.toString());

        return stringBuilder.toString();
    }

    /**
     * Returns the keyword, if present, that matches a given timestamp
     *
     * @param keywordsResultMap   the result map
     * @param speechTimestamp     the speech timestamp
     * @return keyword spotted in speech
     */
    private String obtainSpottedKeywordByTimestamp(final Map<String, List<KeywordsResult>> keywordsResultMap,
                                                   final SpeechTimestamp speechTimestamp) {

        String spottedKeywordMatch = "";
        if (!keywordsResultMap.isEmpty()) {
            Collection<List<KeywordsResult>> keywordsResultCollection = keywordsResultMap.values();
            Iterator<List<KeywordsResult>> keywordsResultIter = keywordsResultCollection.iterator();

            while (keywordsResultIter.hasNext() && spottedKeywordMatch.length() == 0) {
                for (KeywordsResult keywordsResult : keywordsResultIter.next()) {
                    if (keywordsResult.getStartTime().equals(speechTimestamp.getStartTime()) &&
                        keywordsResult.getEndTime().equals((speechTimestamp.getEndTime()))) {
                        spottedKeywordMatch = keywordsResult.getNormalizedText();
                    }
                }
            }
        }

        return spottedKeywordMatch;
    }

    /**
     * Leverages AlchemyLanguage and Concept Insights services to extract and infer keywords from the text of an abstract
     *
     * @param abstractText text of an abstract
     *
     * @return extracted and inferred keywords
     */
    private String[] obtainKeywordsFromAbstractText(final String abstractText) {

        String[] keywordsArray = {};
        List<String> extractedKeywords = new ArrayList<>();
        try {
            extractedKeywords = alchemyLanguageService.getKeywordsFromText(abstractText);
        }
        catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "Exception obtaining extractedKeywords: {0}" + ioe);
        }

        List<String> inferredKeywords;
        inferredKeywords = conceptInsightsService.identifyInferKeywords(abstractText);
        LOGGER.log(Level.INFO, "inferredKeywords: \n" + inferredKeywords);

        // Combine and sort both lists of keywords, returning a String array
        List<String> allKeywords = new ArrayList<>();
        allKeywords.addAll(extractedKeywords);
        allKeywords.addAll(inferredKeywords);
        Collections.sort(allKeywords);
        LOGGER.log(Level.INFO, "allKeywords: \n" + allKeywords);

        List<String> uniqueKeywords = new ArrayList<>();
        for (String keyword : allKeywords) {
            if (!uniqueKeywords.contains(keyword)) {
                uniqueKeywords.add(keyword);
            }
        }
        Collections.sort(uniqueKeywords);
        LOGGER.log(Level.INFO, "uniqueKeywords: \n" + uniqueKeywords);

        return uniqueKeywords.toArray(keywordsArray);
    }

}

package com.devoxx.watson.service;

import com.devoxx.watson.exception.SpeechToTextException;
import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Annotations;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Graph;
import com.ibm.watson.developer_cloud.http.ServiceCall;
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
 * @author Stephan Janssen
 * @author James Weaver
 */
@Component
public class SpeechToTextService {

    private static final Logger LOGGER = Logger.getLogger(SpeechToTextService.class.getName());

    //Note: For now, to test SpeechToTextServiceTest#processAudioFile(), uncomment next two lines and comment Autowired declarations of the same names
    //final AlchemyLanguageService alchemyLanguageService = new AlchemyLanguageService();
    //final ConceptInsightsService conceptInsightsService = new ConceptInsightsService();
    final ConceptInsights conceptInsights = new ConceptInsights();

    @Autowired
    AlchemyLanguageService alchemyLanguageService;

    @Autowired
    ConceptInsightsService conceptInsightsService;

    private SpeechToText speechToText;

    @Autowired
    public void setSpeechToText(final SpeechToText speechToText) {
        this.speechToText = speechToText;
    }

    /**
     * Start speech to text processAudioFile.  Uses a supplied array of keywords to spot in the recognized text.
     *
     * @param audioFile the audio file
     * @param docName the document name
     * @paran keywords optional keywords to spot in the audio
     * @param languageCode
     * @return the transcript
     */
    public String processAudioFile(final File audioFile, final String docName, final String abstractText) throws SpeechToTextException {

        LOGGER.log(Level.INFO, "processAudioFile speech to text service for {0}", docName);

        final String[] keywords = obtainKeywordsFromAbstractText(abstractText);

        final boolean keywordsSupplied = keywords != null && keywords.length > 0;

// TODO - Try this
//        curl -X POST -u <username>:<password>
//                --header "Content-Type: audio/flac"
//                --header "Transfer-Encoding: chunked"
//                --data-binary @<path>0001.flac
//        "https://stream.watsonplatform.net/speech-to-text/api/v1/recognize?continuous=true&timestamps=true&max_alternatives=3"

        final RecognizeOptions.Builder optionsBuilder = new RecognizeOptions.Builder()
            .contentType("audio/ogg")
            .continuous(true)
            .timestamps(true)
            .maxAlternatives(3)
            .interimResults(false);

        if (keywordsSupplied) {
            optionsBuilder.keywords(keywords)
                .keywordsThreshold(0.02);  //TODO: Make this configurable
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
            stringBuilder.append(phrase + "\n");
        }

        LOGGER.log(Level.INFO, "transcript: \n" + stringBuilder.toString());

        return stringBuilder.toString();
    }

    /**
     * Returns the keyword, if present, that matches a given timestamp
     *
     * @param keywordsResultMap
     * @param speechTimestamp
     * @return keyword spotted in speech
     */
    private String obtainSpottedKeywordByTimestamp(Map<String, List<KeywordsResult>> keywordsResultMap,
                                                   SpeechTimestamp speechTimestamp) {
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
    private String[] obtainKeywordsFromAbstractText(String abstractText) {

        // TODO: Remove following and instead leverage Autowired
        alchemyLanguageService.setApikey("3f8ada5e94db2aa57f5b7804be2d1017c3ba8ace");

        String[] keywordsArray = {};
        List<String> extractedKeywords = new ArrayList<>();
        try {
            extractedKeywords = alchemyLanguageService.getKeywordsFromText(abstractText);
        }
        catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "Exception obtaining extractedKeywords: {0}" + ioe);
        }
        //LOGGER.log(Level.INFO, "extractedKeywords: \n" + extractedKeywords);


        // TODO: Remove following and instead leverage Autowired
        conceptInsights.setUsernameAndPassword("384ae42f-bce3-4f47-82d7-69f1a13feb5a", "TPiknk6Whoak");
        conceptInsightsService.setConceptInsights(conceptInsights);

        List<String> inferredKeywords = new ArrayList<>();
        inferredKeywords = conceptInsightsService.identifyInferKeywords(abstractText);
        //LOGGER.log(Level.INFO, "inferredKeywords: \n" + inferredKeywords);


        // Combine and sort both lists of keywords, returning a String array
        List<String> allKeywords = new ArrayList<>();
        allKeywords.addAll(extractedKeywords);
        allKeywords.addAll(inferredKeywords);
        Collections.sort(allKeywords);
        //LOGGER.log(Level.INFO, "allKeywords: \n" + allKeywords);

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

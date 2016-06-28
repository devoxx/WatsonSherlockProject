package com.devoxx.watson.service;

import com.devoxx.watson.exception.SpeechToTextException;
import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Corpus;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author Stephan Janssen
 * @author James Weaver
 */
public class SpeechToTextServiceTest {

    private static final Logger LOGGER = Logger.getLogger(SpeechToTextServiceTest.class.getName());

    private SpeechToTextService speechToTextService = new SpeechToTextService();


    @Before
    public void init() {

        final ConceptInsights conceptInsights = new ConceptInsights();
        conceptInsights.setUsernameAndPassword("384ae42f-bce3-4f47-82d7-69f1a13feb5a", "TPiknk6Whoak");

        final ConceptInsightsService conceptInsightsService = new ConceptInsightsService();
        conceptInsightsService.setConceptInsights(conceptInsights);

        final Corpus corpus = new Corpus(conceptInsightsService.getConceptInsights().getFirstAccountId(), "Corpus_Devoxx1");
        conceptInsightsService.setCorpus(corpus);

        final SpeechToText speechToText = new SpeechToText();
        speechToText.setUsernameAndPassword("d2334b1a-4c18-41df-8dab-3659c0dbfb3d", "dRGqpp6tvCXD");

        final AlchemyLanguageService alchemyLanguageService = new AlchemyLanguageService();
        alchemyLanguageService.setApikey("3f8ada5e94db2aa57f5b7804be2d1017c3ba8ace");

        speechToTextService.setSpeechToText(speechToText);
        speechToTextService.setAlchemyLanguageService(alchemyLanguageService);
        speechToTextService.setConceptInsightsService(conceptInsightsService);
    }

    // Please note that this is an integration test!!
    @Test
    public void processAudioFile() {

        String abstractText = "JDK 9 includes over 80 features, including modules. Some, like project Jigsaw, are huge " +
                "and will impact every Java Developer. Others are improvements in very specific areas that are likely " +
                "to be used directly by only a small percentage of developers. Join us for a guided overview of the " +
                "enhancements coming with JDK 9. This session will provide an quick review of many of the Java Enhancement " +
                "Proposals targeted to JDK 9. The goal of this talk is not to provide a deep dive into any of the " +
                "improvements but to help you discover features that might be of interest to you.";

        URL url = SpeechToTextServiceTest.class.getResource("reinhold-45s.ogg");

        try {
            final File file = new File(url.toURI());

            final String transcript = speechToTextService.processAudioFile(file, "reinhold-45s", abstractText, "en-US_BroadbandModel");

            LOGGER.log(Level.INFO, "transcript: \n" + transcript);

            assertTrue(transcript != null);

            assertFalse(transcript.isEmpty());

            assertTrue(transcript.contains("modules"));

        } catch (URISyntaxException e) {
            fail();
        } catch (SpeechToTextException e) {
            fail(e.toString());
        }
    }

    // Please note that this is an integration test!!
    @Test
    public void processFrenchAudioFile() {

        URL url = SpeechToTextServiceTest.class.getResource("DevoxxFRKeynote.ogg");

        try {
            final File file = new File(url.toURI());

            final String transcript = speechToTextService.processAudioFile(file, "Devoxx FR", null, "fr-FR_BroadbandModel");

            assertTrue(transcript != null);

            assertFalse(transcript.isEmpty());

        } catch (URISyntaxException e) {
            fail();
        } catch (SpeechToTextException e) {
            fail(e.toString());
        }
    }
}

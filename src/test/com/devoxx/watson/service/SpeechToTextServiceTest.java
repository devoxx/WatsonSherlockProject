package com.devoxx.watson.service;

import com.devoxx.watson.exception.SpeechToTextException;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;

import static org.junit.Assert.*;

/**
 * @author Stephan Janssen
 * @author James Weaver
 */
public class SpeechToTextServiceTest {

    private static final Logger LOGGER = Logger.getLogger(SpeechToTextServiceTest.class.getName());

    SpeechToText speechToText = new SpeechToText();

    SpeechToTextService speechToTextService = new SpeechToTextService();

    @Test
    public void processAudioFile() {
        String abstractText = "JDK 9 includes over 80 features, including modules. Some, like project Jigsaw, are huge and will impact every Java Developer. Others are improvements in very specific areas that are likely to be used directly by only a small percentage of developers. Join us for a guided overview of the enhancements coming with JDK 9. This session will provide an quick review of many of the Java Enhancement Proposals targeted to JDK 9. The goal of this talk is not to provide a deep dive into any of the improvements but to help you discover features that might be of interest to you.";

        speechToText.setUsernameAndPassword("d2334b1a-4c18-41df-8dab-3659c0dbfb3d", "dRGqpp6tvCXD");

        speechToTextService.setSpeechToText(speechToText);

        URL url = SpeechToTextServiceTest.class.getResource("reinhold-45s.ogg");

        try {
            final File file = new File(url.toURI());

            final String transcript = speechToTextService.processAudioFile(file, "reinhold-45s", abstractText);

            LOGGER.log(Level.INFO, "transcript: \n" + transcript);

            assertTrue(transcript != null);

            assertFalse(transcript.isEmpty());

        } catch (URISyntaxException e) {
            fail();
        } catch (SpeechToTextException e) {
            fail(e.toString());
        }
    }

    //@Test
    public void processFrenchAudioFile() {
        speechToText.setUsernameAndPassword("d2334b1a-4c18-41df-8dab-3659c0dbfb3d", "dRGqpp6tvCXD");

        speechToTextService.setSpeechToText(speechToText);

        URL url = SpeechToTextServiceTest.class.getResource("DevoxxFRKeynote.ogg");

        try {
            final File file = new File(url.toURI());

            final String transcript = speechToTextService.processAudioFile(file, "Devoxx FR", null);

            assertTrue(transcript != null);

            assertFalse(transcript.isEmpty());

        } catch (URISyntaxException e) {
            fail();
        } catch (SpeechToTextException e) {
            fail(e.toString());
        }
    }
}

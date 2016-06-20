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
        LOGGER.log(Level.INFO, "------ In processAudioFile() -----");

        speechToText.setUsernameAndPassword("d2334b1a-4c18-41df-8dab-3659c0dbfb3d", "dRGqpp6tvCXD");

        speechToTextService.setSpeechToText(speechToText);

        URL url = SpeechToTextServiceTest.class.getResource("reinhold-45s.ogg");

        try {
            final File file = new File(url.toURI());

            final String[] keywords = { "module", "modules", "Java"};

            final String transcript = speechToTextService.processAudioFile(file, "reinhold-45s", keywords);

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

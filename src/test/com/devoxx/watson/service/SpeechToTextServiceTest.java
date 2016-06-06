package com.devoxx.watson.service;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author Stephan Janssen
 */
public class SpeechToTextServiceTest {

    @Test
    public void processAudioFile() {

        SpeechToText speechToText = new SpeechToText();
        speechToText.setUsernameAndPassword("d2334b1a-4c18-41df-8dab-3659c0dbfb3d", "dRGqpp6tvCXD");

        final SpeechToTextService speechToTextService = new SpeechToTextService();
        speechToTextService.setSpeechToText(speechToText);

        URL url = SpeechToTextServiceTest.class.getResource("MarkReinholdKeynote1m.ogg");

        try {
            final File file = new File(url.toURI());

            final String transcript = speechToTextService.processAudioFile(file, "Mark Reinhold");

            assertTrue(transcript != null);

            assertFalse(transcript.isEmpty());

        } catch (URISyntaxException e) {
            fail();
        } catch (SpeechToTextException e) {
            fail(e.toString());
        }
    }
}

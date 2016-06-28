package com.devoxx.watson.controller;

import com.devoxx.watson.exception.SpeechToTextException;
import com.devoxx.watson.service.ConceptInsightsService;
import com.devoxx.watson.service.LanguageTranslateService;
import com.devoxx.watson.service.SpeechToTextService;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Document;
import com.ibm.watson.developer_cloud.language_translation.v2.model.Language;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Stephan Janssen
 */
public class WatsonControllerTest {

    private WatsonController watsonController = new WatsonController();

    @Test
    public void processSpeechToTextUsingFrenchText() throws SpeechToTextException {

        final SpeechToTextService mockSpeechToTextService = mock(SpeechToTextService.class);
        watsonController.setSpeechToTextService(mockSpeechToTextService);

        final LanguageTranslateService mockLanguageTranslateService = mock(LanguageTranslateService.class);
        watsonController.setLanguageTranslateService(mockLanguageTranslateService);

        when(mockSpeechToTextService.processAudioFile(isA(File.class), isA(String.class), isA(String.class), isA(String.class))).thenReturn("test");
        when(mockLanguageTranslateService.translate(isA(String.class), eq(Language.FRENCH), eq(Language.ENGLISH))).thenReturn("le test :)");

        final String translatedTranscript = watsonController.processSpeechToText(new File("tmp"), "docName", "abstract", "fr-FR_BroadbandModel");
        assertEquals("le test :)", translatedTranscript);
    }

    @Test
    public void processSpeechToTextUsingEnglishText() throws SpeechToTextException {

        final SpeechToTextService mockSpeechToTextService = mock(SpeechToTextService.class);
        watsonController.setSpeechToTextService(mockSpeechToTextService);

        when(mockSpeechToTextService.processAudioFile(isA(File.class), isA(String.class), isA(String.class), isA(String.class))).thenReturn("test");

        final String translatedTranscript = watsonController.processSpeechToText(new File("tmp"), "docName", "abstract", "en-US_BroadbandModel");
        assertEquals("test", translatedTranscript);
    }

    @Test
    public void findDocument() {

        final ConceptInsightsService mockConceptInsightsService = mock(ConceptInsightsService.class);
        watsonController.setConceptInsightsService(mockConceptInsightsService);

        final Document document1 = new Document();

        when(mockConceptInsightsService.findDocument(isA(String.class))).thenReturn(document1);

        final Document foundDocument = watsonController.findDocument("123");

        assertEquals(document1, foundDocument);
    }

    @Test
    public void findEmptyDocument() {

        final Document document = watsonController.findDocument("");

        assertNull(document);
    }

    @Test
    public void findNullDocument() {

        final Document document = watsonController.findDocument(null);

        assertNull(document);
    }
}

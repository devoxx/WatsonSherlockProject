package com.devoxx.watson.service;

import com.devoxx.watson.model.AlchemyContent;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Stephan Janssen
 * @author James Weaver
 */
public class AlchemyLanguageServiceTest {

    private static final Logger LOGGER = Logger.getLogger(SpeechToTextServiceTest.class.getName());

    final AlchemyLanguageService alchemyAPIService = new AlchemyLanguageService();

    //@Test
    public void process() {

        alchemyAPIService.setApikey("446dc444d593ed09eece2c66476c87c269c37896");
        alchemyAPIService.process(new AlchemyContent("link"));
    }

    //@Test
    public void getThumbnailKeywords() throws IOException {

        alchemyAPIService.setApikey("3f8ada5e94db2aa57f5b7804be2d1017c3ba8ace");
        final String thumbnailKeywords =
                alchemyAPIService.getThumbnailKeywords("https://www.voxxed.com/wp-content/uploads/2016/04/Screen-Shot-2016-04-18-at-14.49.05.jpg");
    }

    @Test
    public void getAlchemyDataFromText() throws IOException {

        alchemyAPIService.setApikey("3f8ada5e94db2aa57f5b7804be2d1017c3ba8ace");
        final String abstractText = "JDK 9 includes over 80 features. Some, like project Jigsaw, are huge and will impact every Java Developer. Others are improvements in very specific areas that are likely to be used directly by only a small percentage of developers. Join us for a guided overview of the enhancements coming with JDK 9. This session will provide an quick review of many of the Java Enhancement Proposals targeted to JDK 9. The goal of this talk is not to provide a deep dive into any of the improvements but to help you discover features that might be of interest to you.";

        final List<String> keywords = alchemyAPIService.getKeywordsFromText(abstractText);

        LOGGER.log(Level.INFO, "keywords: \n" + keywords);

        assertTrue(keywords != null);

        assertFalse(keywords == null);
    }
}

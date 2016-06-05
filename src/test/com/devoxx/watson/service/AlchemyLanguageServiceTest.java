package com.devoxx.watson.service;

import com.devoxx.watson.model.AlchemyContent;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Stephan Janssen
 */
public class AlchemyLanguageServiceTest {

    final AlchemyLanguageService alchemyAPIService = new AlchemyLanguageService();

    @Test
    public void process() {

        alchemyAPIService.setApikey("446dc444d593ed09eece2c66476c87c269c37896");
        alchemyAPIService.process(new AlchemyContent("link"));
    }

    @Test
    public void getThumbnailKeywords() throws IOException {

        alchemyAPIService.setApikey("3f8ada5e94db2aa57f5b7804be2d1017c3ba8ace");
        final String thumbnailKeywords =
                alchemyAPIService.getThumbnailKeywords("https://www.voxxed.com/wp-content/uploads/2016/04/Screen-Shot-2016-04-18-at-14.49.05.jpg");
    }
}

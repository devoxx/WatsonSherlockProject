package com.devoxx.watson.service;

import com.devoxx.watson.model.AlchemyContent;
import org.junit.Test;

/**
 * @author Stephan Janssen
 */
public class AlchemyLanguageServiceTest {

    @Test
    public void process() {
        final AlchemyLanguageService alchemyAPIService = new AlchemyLanguageService();
        alchemyAPIService.setApikey("446dc444d593ed09eece2c66476c87c269c37896");
        alchemyAPIService.process(new AlchemyContent("link"));
    }

}

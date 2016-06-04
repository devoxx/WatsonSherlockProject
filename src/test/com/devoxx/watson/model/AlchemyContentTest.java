package com.devoxx.watson.model;

import org.junit.Test;

/**
 * @author Stephan Janssen
 */
public class AlchemyContentTest {

    @Test
    public void testPublicationDateFormat() {

        final AlchemyContent alchemyContent = new AlchemyContent("link");
        alchemyContent.setPublicationDate("20160112T000000");
        assert(alchemyContent.getPublicationDate().equals("12 Jan 2016"));
    }
}

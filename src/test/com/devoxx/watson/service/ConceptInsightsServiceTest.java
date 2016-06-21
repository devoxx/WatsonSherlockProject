package com.devoxx.watson.service;

import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import org.junit.Test;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Stephan Janssen
 * @author James Weaver
 */
public class ConceptInsightsServiceTest {

    private static final Logger LOGGER = Logger.getLogger(SpeechToTextServiceTest.class.getName());

    ConceptInsights conceptInsights = new ConceptInsights();

    ConceptInsightsService conceptInsightsService = new ConceptInsightsService();


    @Test
    public void createDocument() {

    }

    @Test
    public void identifyInferKeywords() {
        conceptInsights.setUsernameAndPassword("384ae42f-bce3-4f47-82d7-69f1a13feb5a", "TPiknk6Whoak");

        conceptInsightsService.setConceptInsights(conceptInsights);

        final String abstractText = "JDK 9 includes over 80 features. Some, like project Jigsaw, are huge and will impact every Java Developer. Others are improvements in very specific areas that are likely to be used directly by only a small percentage of developers. Join us for a guided overview of the enhancements coming with JDK 9. This session will provide an quick review of many of the Java Enhancement Proposals targeted to JDK 9. The goal of this talk is not to provide a deep dive into any of the improvements but to help you discover features that might be of interest to you.";

        final List<String> keywords = conceptInsightsService.identifyInferKeywords(abstractText);

        LOGGER.log(Level.INFO, "keywords: \n" + keywords);

        assertTrue(keywords != null);

        assertFalse(keywords == null);

    }
}

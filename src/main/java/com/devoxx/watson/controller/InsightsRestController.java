package com.devoxx.watson.controller;

import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Stephan Janssen
 */
@RestController
public class InsightsRestController {

    private static final Logger LOGGER = Logger.getLogger(InsightsRestController.class.getName());

    @Autowired
    private ConceptInsights conceptInsights;

    @Autowired
    private Corpus corpus;

    @RequestMapping(value = "/search/{value}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Result> searchInsights(@PathVariable("value") String searchText) {

        LOGGER.log(Level.INFO, "Label search for \"{0}\"", searchText);

        Map<String, Object> searchGraphConceptByLabelParams = new HashMap<>();
        searchGraphConceptByLabelParams.put("query", searchText);
        searchGraphConceptByLabelParams.put("prefix", true);
        searchGraphConceptByLabelParams.put("limit", 10);

        RequestedFields concept_fields = new RequestedFields();
        concept_fields.include("link");
        concept_fields.include("\"abstract\":1");
        RequestedFields document_fields = new RequestedFields();
        document_fields.include("\"user_fields\":1");

        searchGraphConceptByLabelParams.put("concept_fields", concept_fields);

        Matches matches = conceptInsights.searchGraphsConceptByLabel(Graph.WIKIPEDIA, searchGraphConceptByLabelParams);

        LOGGER.log(Level.INFO, "Found {0} matches", matches.getMatches().size());

        List<String> ids = new ArrayList<>();

        for (Concept concept : matches.getMatches()) {
            ids.add(concept.getId());
            break;
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ConceptInsights.IDS, ids);
        parameters.put(ConceptInsights.LIMIT, 20);

        RequestedFields requestedFields = new RequestedFields();
        requestedFields.include("\"user_fields\":1");
        parameters.put(ConceptInsights.DOCUMENT_FIELDS, requestedFields);

        QueryConcepts queryConcepts = conceptInsights.conceptualSearch(corpus, parameters);

        // output results
        LOGGER.log(Level.INFO, "Found {0} matches for conceptual search", queryConcepts.getResults().size());
        return queryConcepts.getResults();
    }
}
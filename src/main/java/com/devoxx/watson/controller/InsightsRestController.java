package com.devoxx.watson.controller;

import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.*;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Stephan Janssen
 */
@RequestMapping("/api/")
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
    public ResponseEntity<List<Result>> searchInsights(@PathVariable("value") String searchText) {

        LOGGER.log(Level.INFO, "Label search for \"{0}\"", searchText);

        Map<String, Object> searchGraphConceptByLabelParams = new HashMap<>();
        searchGraphConceptByLabelParams.put(ConceptInsights.QUERY, searchText);
        searchGraphConceptByLabelParams.put(ConceptInsights.PREFIX, true);
        searchGraphConceptByLabelParams.put(ConceptInsights.LIMIT, 10);

        RequestedFields concept_fields = new RequestedFields();
        concept_fields.include("link");
        concept_fields.include("\"abstract\":1");
        RequestedFields document_fields = new RequestedFields();
        document_fields.include("\"user_fields\":1");

        searchGraphConceptByLabelParams.put(ConceptInsights.CONCEPT_FIELDS, concept_fields);

        ServiceCall<Matches> serviceCall = conceptInsights.searchGraphsConceptByLabel(Graph.WIKIPEDIA, searchGraphConceptByLabelParams);

        final Matches matches = serviceCall.execute();

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

        ServiceCall<QueryConcepts> conceptsServiceCall = conceptInsights.conceptualSearch(corpus, parameters);

        final QueryConcepts queryConcepts = conceptsServiceCall.execute();
        // output results
        LOGGER.log(Level.INFO, "Found {0} matches for conceptual search", queryConcepts.getResults().size());
        return new ResponseEntity<>(queryConcepts.getResults(), HttpStatus.OK);
    }

    @RequestMapping(value = "/documents",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getDocuments(@RequestParam(value="limit", required=false, defaultValue = "20") int limit) {
        final Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ConceptInsights.LIMIT, limit);
        return new ResponseEntity<>(conceptInsights.listDocuments(corpus, queryParameters), HttpStatus.OK);
    }

    @RequestMapping(value = "/document/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getDocument(@PathVariable("id") String documentId) {

        final Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ConceptInsights.LIMIT, 20);

        final ServiceCall<Documents> serviceCall = conceptInsights.listDocuments(corpus, queryParameters);

        final Documents documents = serviceCall.execute();
        final List<String> documentList = documents.getDocuments();
        for (String id : documentList) {
            if (id.endsWith(documentId)) {
                Document doc = new Document();
                doc.setId(id);
                return new ResponseEntity<>(conceptInsights.getDocument(doc), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

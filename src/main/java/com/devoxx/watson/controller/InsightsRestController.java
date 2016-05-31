package com.devoxx.watson.controller;

import com.ibm.watson.developer_cloud.concept_insights.v2.model.Document;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Stephan Janssen
 */
@RequestMapping("/api/")
@RestController
class InsightsRestController {

    @Autowired
    private ConceptInsightsService conceptInsightsService;

    @RequestMapping(value = "/search/{value}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchInsights(@PathVariable("value") String searchText) {

        final List<Result> results = conceptInsightsService.searchDocuments(searchText);

        if (results == null) {
            return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(results, HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/documents",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getDocuments(@RequestParam(value="limit", required=false, defaultValue = "20") int limit) {

        return new ResponseEntity<>(conceptInsightsService.getAllDocuments(limit), HttpStatus.OK);
    }


    @RequestMapping(value = "/document/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getDocument(@PathVariable("id") String documentId) {

        final Document foundDocument = conceptInsightsService.findDocument(documentId);

        if (foundDocument == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(foundDocument, HttpStatus.OK);
        }
    }
}

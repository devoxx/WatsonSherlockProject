package com.devoxx.watson.controller;

import com.devoxx.watson.model.AlchemyContent;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Document;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Result;
import com.sun.istack.internal.NotNull;
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
class WatsonRestController {

    @Autowired
    private WatsonController watsonController;

    /**
     * Search Watson Insights.
     *
     * @param searchText the search text
     * @return results
     */
    @RequestMapping(value = "/search/{value}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchInsights(@PathVariable("value") @NotNull String searchText) {

        final List<Result> results = watsonController.searchDocuments(searchText);

        if (results == null) {
            return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(results, HttpStatus.OK);
        }
    }

    /**
     * Get all documents.
     *
     * @param limit limit to return
     * @return list of documents
     */
    @RequestMapping(value = "/documents",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getDocuments(@RequestParam(value = "limit", required = false, defaultValue = "20") int limit) {

        return new ResponseEntity<>(watsonController.getAllDocuments(limit), HttpStatus.OK);
    }

    /**
     * Get a document.
     *
     * @param documentId the document identifier
     * @return a document
     */
    @RequestMapping(value = "/document/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getDocument(@PathVariable("id") @NotNull String documentId) {

        final Document foundDocument = watsonController.findDocument(documentId);

        if (foundDocument == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(foundDocument, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/article/", method = RequestMethod.POST)
    public ResponseEntity uploadArticleLink(@RequestParam("link") String link) {

        try {
            final AlchemyContent content = watsonController.processLink(link);

            return new ResponseEntity<>(content.getTitle(), HttpStatus.CREATED);
        } catch (DocumentAlreadyExistsException e) {
            return new ResponseEntity<>(link, HttpStatus.NOT_MODIFIED);
        }

    }
}

package com.devoxx.watson.controller;

import com.devoxx.watson.exception.ArticleTextExtractionException;
import com.devoxx.watson.exception.DocumentAlreadyExistsException;
import com.devoxx.watson.model.AlchemyContent;
import com.devoxx.watson.model.ConversationModel;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Document;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Result;
import com.ibm.watson.developer_cloud.dialog.v1.model.Conversation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Contains the REST methods for uploading documents and audio files but also Dialog methods.
 *
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
    public ResponseEntity searchInsights(@PathVariable("value") String searchText) {

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
    public ResponseEntity getDocument(@PathVariable("id") String documentId) {

        final Document foundDocument = watsonController.findDocument(documentId);

        if (foundDocument == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(foundDocument, HttpStatus.OK);
        }
    }

    /**
     * Upload an HTML article link.
     *
     * @param link  the article URL
     * @return created, not modified or internal error status codes
     */
    @RequestMapping(value = "/article/", method = RequestMethod.POST)
    public ResponseEntity uploadArticleLink(@RequestParam("link") String link) {

        try {

            final AlchemyContent content = watsonController.processLink(link);

            return new ResponseEntity<>(content.getTitle(), HttpStatus.CREATED);

        } catch (DocumentAlreadyExistsException |
                 DocumentThumbnailKeywordsException e) {

            return new ResponseEntity<>(link, HttpStatus.NOT_MODIFIED);

        } catch (ArticleTextExtractionException e) {

            return new ResponseEntity<>(link, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Initializes chat with Watson
     *
     * This initiates the chat with Watson by requesting for a client id and conversation id(to be used in subsequent API
     * calls) and a response message to be displayed to the user. If it's a returning user, it sets the First_Time profile
     * variable to "No" so that the user is not taken through the hand-holding process.
     *
     * @return a response contains {@code ConversationModel}
     */
    @RequestMapping(value = "/initdialog/", method = RequestMethod.POST)
    public ResponseEntity startConversation(@RequestParam("firstTimeUser") boolean firstTimeUser) {

        final String dialogId = UUID.randomUUID().toString();

        final ConversationModel conversationModel = watsonController.initConversation(firstTimeUser, dialogId);

        return new ResponseEntity<>(conversationModel, HttpStatus.CREATED);
    }
}

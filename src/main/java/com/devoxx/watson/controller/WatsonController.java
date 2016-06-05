package com.devoxx.watson.controller;

import com.devoxx.watson.model.AlchemyContent;
import com.devoxx.watson.service.AlchemyLanguageService;
import com.devoxx.watson.service.ConceptInsightsService;
import com.devoxx.watson.util.SoupUtil;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Document;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Documents;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Stephan Janssen
 */
@Component
public class WatsonController {

    @Autowired
    ConceptInsightsService conceptInsightsService;

    @Autowired
    AlchemyLanguageService alchemyLanguageService;

    /**
     * Process an article or video link.
     *
     * @param link the article or presentation link
     * @return the alchemy content
     */
    AlchemyContent processLink(final String link) throws DocumentAlreadyExistsException {

        final AlchemyContent content = new AlchemyContent(link);

        if (conceptInsightsService.findDocument(content.getId()) == null) {

            alchemyLanguageService.process(content);

            content.setThumbnail(SoupUtil.getThumbnail(link));

            conceptInsightsService.createDocument(content);

            return content;
        } else {
            throw new DocumentAlreadyExistsException();
        }
    }

    /**
     * Search the Corpus documents.
     *
     * @param searchValue   the search value
     * @return list of document results
     */
    List<Result> searchDocuments(final String searchValue) {
        if (searchValue.isEmpty()) {
            return null;
        } else {
            return conceptInsightsService.searchDocuments(searchValue);
        }
    }

    /**
     * Return all Corpus documents
     *
     * @param limit limit the result set
     * @return list of Corpus documents
     */
    Documents getAllDocuments(final int limit) {
        return conceptInsightsService.getAllDocuments(limit);
    }

    /**
     * Find a document based on unique id.
     *
     * @param documentId    the document id
     * @return the relate Corpus document
     */
    Document findDocument(final String documentId) {
        if (documentId.isEmpty()) {
            return null;
        } else {
            return conceptInsightsService.findDocument(documentId);
        }
    }
}

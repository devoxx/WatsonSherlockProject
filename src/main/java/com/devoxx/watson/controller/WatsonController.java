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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.devoxx.watson.service.ConceptInsightsService.USER_FIELD_THUMBNAIL;
import static com.devoxx.watson.service.ConceptInsightsService.USER_FIELD_THUMBNAIL_KEYWORDS;

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
     * @throws DocumentAlreadyExistsException
     * @throws DocumentThumbnailKeywordsException
     * @return the alchemy content
     */
    AlchemyContent processLink(final String link)
            throws DocumentAlreadyExistsException, DocumentThumbnailKeywordsException {

        final AlchemyContent content = new AlchemyContent(link);

        final Document document = conceptInsightsService.findDocument(content.getId());

        if (document == null) {

            content.setThumbnail(SoupUtil.getThumbnail(link));

            alchemyLanguageService.process(content);

            conceptInsightsService.createDocument(content);

            return content;

        }

        // Does document have thumbnail keywords?
        final Map<String, String> userFields = document.getUserFields();

        if (!userFields.containsKey(USER_FIELD_THUMBNAIL_KEYWORDS)) {
            try {
                final String thumbnailKeywords = alchemyLanguageService.getThumbnailKeywords(userFields.get(USER_FIELD_THUMBNAIL));

                userFields.put(USER_FIELD_THUMBNAIL_KEYWORDS, thumbnailKeywords);

                conceptInsightsService.updateDocument(document);

            } catch (IOException e) {
                throw new DocumentThumbnailKeywordsException();
            }
        } else {
            throw new DocumentAlreadyExistsException();
        }

        return content;
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

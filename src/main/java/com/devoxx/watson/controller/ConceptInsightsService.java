package com.devoxx.watson.controller;

import com.devoxx.watson.util.SoupUtil;
import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.*;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

/**
 * @author Stephan Janssen
 */
@Component
class ConceptInsightsService {

    private static final Logger LOGGER = Logger.getLogger(ProcessAudioFile.class.getName());
    private static final int MAX_DOCUMENTS_TO_SEARCH = 2000;

    @Autowired
    private ConceptInsights conceptInsights;

    @Autowired
    private Corpus corpus;

    /**
     * Concept insights service.
     */
    void createDocument(final String docName, final String link, final String text) {

        LOGGER.log(Level.INFO, "create document for {0}", docName);
        Document newDocument = new Document(corpus, createDocumentId(docName));
        newDocument.setName(docName);
        newDocument.setLabel(docName);

        final Map<String, String> userFields = new HashMap<>();
        userFields.put("link", link);
        newDocument.setUserFields(userFields);

        newDocument.addParts(new Part("part_", text, HttpMediaType.TEXT_PLAIN));

        LOGGER.info("Create document");
        conceptInsights.createDocument(newDocument).execute();
    }

    /**
     * Create a Insights document based on a hyper link.
     *
     * @param hyperLink the hyperlink to an HTML article
     */
    org.jsoup.nodes.Document createDocument(final String hyperLink) {

        final org.jsoup.nodes.Document document = SoupUtil.getDocument(hyperLink);

        String title = document.title();

        if (!documentExists(title)) {

            final Elements select = document.select("div.amp-wp-content p");

            if (select != null && !select.isEmpty()) {
                createDocument(title, hyperLink, select.text());

                return document;
            }
        }

        return null;
    }

    /**
     * Helper method
     * @param label the document label is used to create the unique document identifier
     * @return true when document exists
     */
    private boolean documentExists(final String label) {

        return findDocument(createDocumentId(label)) != null;
    }

    /**
     * Create a unique document identifier.
     *
     * @param docName   the document name
     * @return a positive hashcode value
     */
    private String createDocumentId(String docName) {

        return String.valueOf(abs(docName.hashCode()));
    }

    /**
     * Find document by identifier.
     *
     * @param documentId    the document id
     * @return the related document
     */
    Document findDocument(final String documentId) {

        final Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put(ConceptInsights.LIMIT, MAX_DOCUMENTS_TO_SEARCH);

        final Documents documents = conceptInsights.listDocuments(corpus, queryParameters).execute();

        final Optional<String> docId = documents.getDocuments().stream()
                .filter(e -> e.endsWith(documentId))
                .findFirst();

        if (docId.isPresent()) {
            Document doc = new Document();
            doc.setId(docId.get());
            return conceptInsights.getDocument(doc).execute();
        } else {
            return null;
        }
    }

    /**
     * Return all documents.
     *
     * @param limit the limit to return
     * @return list of documents
     */
    Documents getAllDocuments(int limit) {

        final Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put(ConceptInsights.LIMIT,limit);

        return conceptInsights.listDocuments(corpus, queryParameters).execute();
    }


    /**
     * Search the documents.
     *
     * @param searchText    the search text
     * @return list of documents
     */
    List<Result> searchDocuments(final String searchText) {

        LOGGER.log(Level.INFO, "Label search for \"{0}\"", searchText);

        Map<String, Object> searchGraphConceptByLabelParams = new HashMap<>();
        searchGraphConceptByLabelParams.put(ConceptInsights.QUERY, searchText);
        searchGraphConceptByLabelParams.put(ConceptInsights.PREFIX, true);
        searchGraphConceptByLabelParams.put(ConceptInsights.LIMIT, 10);

        RequestedFields concept_fields = new RequestedFields();
        concept_fields.include("link");
        concept_fields.include("abstract");

        searchGraphConceptByLabelParams.put(ConceptInsights.CONCEPT_FIELDS, concept_fields);

        final Matches matches =
                conceptInsights.searchGraphsConceptByLabel(Graph.WIKIPEDIA, searchGraphConceptByLabelParams)
                               .execute();

        LOGGER.log(Level.INFO, "Found {0} matches", matches.getMatches().size());

        List<String> ids = matches.getMatches()
                                  .stream()
                                  .map(Concept::getId)
                                  .collect(Collectors.toList());

        if (ids.isEmpty()) {
            return null;
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ConceptInsights.IDS, ids);
        parameters.put(ConceptInsights.LIMIT, 20);

        RequestedFields requestedFields = new RequestedFields();
        requestedFields.include("user_fields");
        parameters.put(ConceptInsights.DOCUMENT_FIELDS, requestedFields);

        final QueryConcepts queryConcepts = conceptInsights.conceptualSearch(corpus, parameters).execute();

        LOGGER.log(Level.INFO, "Found {0} matches for conceptual search", queryConcepts.getResults().size());

        return queryConcepts.getResults();
    }
}

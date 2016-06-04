package com.devoxx.watson.controller;

import com.devoxx.watson.model.AlchemyContent;
import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.*;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Stephan Janssen
 */
@Component
class ConceptInsightsService {

    private static final Logger LOGGER = Logger.getLogger(ProcessAudioFile.class.getName());

    private static final int MAX_DOCUMENTS_TO_SEARCH = 2000;
    private static final int ONLY_USE_TWO_CONCEPTS = 2;
    private static final int RETURN_ONLY_TEN_CONCEPTS = 10;

    private static final String DEVOXX_PICTURE = "https://pbs.twimg.com/media/Ciph_FlWkAAF4D-.jpg";
    private static final String USER_FIELD_THUMBNAIL = "thumbnail";
    private static final String USER_FIELD_LINK = "link";
    private static final String USER_FIELD_LANGUAGE = "language";
    private static final String USER_FIELD_PUBLICATION_DATE = "publicationDate";
    private static final String USER_FIELDS_AUTHORS = "authors";
    private static final String USER_FIELDS_SENTIMENT = "sentiment";
    private static final String USER_FIELD_EMOTIONS = "emotions";

    @Autowired
    private ConceptInsights conceptInsights;

    @Autowired
    private Corpus corpus;

    /**
     * Create document for audio file.
     *
     * @param docName   the document name
     * @param link      the YouTube link
     * @param text      the transcript
     * @param speakers  the speakers
     */
    void createDocument(final String docName,
                        final String link,
                        final String text,
                        final String speakers) {

        LOGGER.log(Level.INFO, "create document for {0}", docName);
        Document newDocument = new Document(corpus, String.valueOf(Math.abs(docName.hashCode())));
        newDocument.setName(docName);
        newDocument.setLabel(docName);

        final Map<String, String> userFields = new HashMap<>();
        userFields.put(USER_FIELD_LINK, link);
        userFields.put(USER_FIELDS_AUTHORS, speakers);
        userFields.put(USER_FIELD_THUMBNAIL, DEVOXX_PICTURE);   // Default thumbnail
        newDocument.setUserFields(userFields);

        newDocument.addParts(new Part("part_", text, HttpMediaType.TEXT_PLAIN));

        LOGGER.info("Create document");
        conceptInsights.createDocument(newDocument).execute();
    }

    /**
     * Create a Watson Corpus document.
     *
     * @param content the alchemy content
     */
    void createDocument(final AlchemyContent content) {

        LOGGER.log(Level.INFO, "create document for {0}", content.getTitle());

        Document newDocument = new Document(corpus, content.getId());

        newDocument.setName(content.getTitle());
        newDocument.setLabel(content.getTitle());

        final Map<String, String> userFields = new HashMap<>();
        userFields.put(USER_FIELD_LINK, content.getLink());

        if (content.getThumbnail().isEmpty()) {
            userFields.put(USER_FIELD_THUMBNAIL, DEVOXX_PICTURE);
        } else {
            userFields.put(USER_FIELD_THUMBNAIL, content.getThumbnail());
        }

        // Alchemy user fields
        userFields.put(USER_FIELD_LANGUAGE, content.getLanguage());
        userFields.put(USER_FIELD_PUBLICATION_DATE, content.getPublicationDate());
        userFields.put(USER_FIELDS_AUTHORS, content.getAuthors());
        userFields.put(USER_FIELDS_SENTIMENT, content.getSentiment());
        userFields.put(USER_FIELD_EMOTIONS, content.getEmotions());

        newDocument.setUserFields(userFields);
        newDocument.addParts(new Part("part", content.getContent(), HttpMediaType.TEXT_PLAIN));

        LOGGER.info("Create document");
        conceptInsights.createDocument(newDocument).execute();
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
        searchGraphConceptByLabelParams.put(ConceptInsights.LIMIT, ONLY_USE_TWO_CONCEPTS);

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
        parameters.put(ConceptInsights.LIMIT, RETURN_ONLY_TEN_CONCEPTS);

        RequestedFields requestedFields = new RequestedFields();
        requestedFields.include("user_fields");
        parameters.put(ConceptInsights.DOCUMENT_FIELDS, requestedFields);

        final QueryConcepts queryConcepts = conceptInsights.conceptualSearch(corpus, parameters).execute();

        LOGGER.log(Level.INFO, "Found {0} matches for conceptual search", queryConcepts.getResults().size());

        return queryConcepts.getResults();
    }
}

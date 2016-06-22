package com.devoxx.watson.service;

import com.devoxx.watson.model.AlchemyContent;
import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.*;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Stephan Janssen
 * @author James Weaver
 */
@Component
public class ConceptInsightsService {

    private static final Logger LOGGER = Logger.getLogger(ConceptInsightsService.class.getName());

    private static final int MAX_DOCUMENTS_TO_SEARCH = 2000;
    private static final int ONLY_USE_TWO_CONCEPTS = 2;
    private static final int RETURN_ONLY_TEN_CONCEPTS = 10;

    private static final String DEVOXX_PICTURE = "https://pbs.twimg.com/media/Ciph_FlWkAAF4D-.jpg";

    private static final String USER_FIELD_LINK = "link";
    private static final String USER_FIELD_LANGUAGE = "language";
    private static final String USER_FIELD_PUBLICATION_DATE = "publicationDate";
    private static final String USER_FIELDS_AUTHORS = "authors";
    private static final String USER_FIELDS_SENTIMENT = "sentiment";
    private static final String USER_FIELD_EMOTIONS = "emotions";
    public static final String USER_FIELD_THUMBNAIL = "thumbnail";
    public static final String USER_FIELD_THUMBNAIL_KEYWORDS = "thumbnailKeywords";

    private ConceptInsights conceptInsights;

    @Autowired
    public void setConceptInsights(final ConceptInsights conceptInsights) {
        this.conceptInsights = conceptInsights;
    }

    @Autowired
    private Corpus corpus;

    /**
     * Create a Watson Corpus document.
     *
     * @param content the alchemy content
     */
    public void createDocument(final AlchemyContent content) {

        LOGGER.log(Level.INFO, "create document for {0}", content.getTitle());

        Document newDocument = new Document(corpus, content.getId());

        newDocument.setName(content.getTitle());
        newDocument.setLabel(content.getTitle());

        final Map<String, String> userFields = new HashMap<>();
        userFields.put(USER_FIELD_LINK, content.getLink());

        if (content.getThumbnail() == null ||
            content.getThumbnail().isEmpty()) {
            userFields.put(USER_FIELD_THUMBNAIL, DEVOXX_PICTURE);
        } else {
            userFields.put(USER_FIELD_THUMBNAIL, content.getThumbnail());
        }

        // Alchemy user fields
        if (!content.getLanguage().isEmpty()) {
            userFields.put(USER_FIELD_LANGUAGE, content.getLanguage());
        }

        if (content.getPublicationDate() != null) {
            userFields.put(USER_FIELD_PUBLICATION_DATE, content.getPublicationDate());
        }

        userFields.put(USER_FIELDS_AUTHORS, content.getAuthors());
        userFields.put(USER_FIELDS_SENTIMENT, content.getSentiment());
        userFields.put(USER_FIELD_EMOTIONS, content.getEmotions());

        if (content.getThumbnailKeywords() != null &&
            !content.getThumbnailKeywords().isEmpty()) {
            userFields.put(USER_FIELD_THUMBNAIL_KEYWORDS, content.getThumbnailKeywords());
        }

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
    public Document findDocument(final String documentId) {

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
    public Documents getAllDocuments(int limit) {

        final Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put(ConceptInsights.LIMIT,limit);

        return conceptInsights.listDocuments(corpus, queryParameters).execute();
    }


    /**
     * Update an existing document.
     *
     * @param document  the updated document
     */
    public void updateDocument(final Document document) {
        conceptInsights.updateDocument(document).execute();
    }


    /**
     * Search the documents.
     *
     * @param searchText    the search text
     * @return list of documents
     */
    public List<Result> searchDocuments(final String searchText) {

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

    /**
     * Given the text of an abstract, identify and infer keywords useful for recognizing
     *
     * @param text text of an abstract
     *
     * @return sorted list of unique keywords
     */
    public List<String> identifyInferKeywords(String text) {
        String abstractText = (text == null) ? "keyword" : text;
        List<String> keywords = new ArrayList<>();
        final ServiceCall<Annotations> annotations = conceptInsights.annotateText(Graph.WIKIPEDIA, abstractText);
        for (ScoredConcept scoredConcept : annotations.execute().getAnnotations()) {
            String label = scoredConcept.getConcept().getLabel();
            label = label.replace("(", "");
            label = label.replace(")", "");
            String[] tokens = label.split(" ");
            for (String token : tokens) {
                if (!keywords.contains(token)) {
                    keywords.add(token);
                }
            }
        }
        Collections.sort(keywords);
        return keywords;
    }
}

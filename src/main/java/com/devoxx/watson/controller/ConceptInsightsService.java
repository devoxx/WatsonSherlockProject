package com.devoxx.watson.controller;

import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.*;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private ConceptInsights conceptInsights;

    @Autowired
    private Corpus corpus;

    /**
     * Concept insights service.
     */
    void createDocument(final String docName, final String link, final String text) {

        LOGGER.log(Level.INFO, "create document for {0}", docName);
        Document newDocument = new Document(corpus, "" + abs(docName.hashCode()));
        newDocument.setName(docName);
        newDocument.setLabel(docName);

        final Map<String, String> userFields = new HashMap<>();
        userFields.put("link", link);
        newDocument.setUserFields(userFields);

        newDocument.addParts(new Part("part_", text, HttpMediaType.TEXT_PLAIN));

        LOGGER.info("Create document");
        final ServiceCall<Void> documentServiceCall = conceptInsights.createDocument(newDocument);
        documentServiceCall.execute();
    }

    /**
     * Helper method
     * @param label the document label is used to create the unique document identifier
     * @return true when document exists
     */
    boolean documentExists(final String label) {

        return findDocument("" + label.hashCode()) != null;
    }

    /**
     * Find document by identifier.
     *
     * @param documentId    the document id
     * @return the related document
     */
    Document findDocument(final String documentId) {

        final Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put(ConceptInsights.LIMIT, 20);

        final ServiceCall<Documents> serviceCall = conceptInsights.listDocuments(corpus, queryParameters);

        final Documents documents = serviceCall.execute();
        final List<String> documentList = documents.getDocuments();

        for (String id : documentList) {

            if (id.endsWith(documentId)) {

                Document doc = new Document();
                doc.setId(id);

                final ServiceCall<Document> documentServiceCall = conceptInsights.getDocument(doc);

                return documentServiceCall.execute();
            }
        }

        return null;
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

        final ServiceCall<Documents> serviceCall = conceptInsights.listDocuments(corpus, queryParameters);

        return serviceCall.execute();
    }


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

        ServiceCall<Matches> serviceCall =
                conceptInsights.searchGraphsConceptByLabel(Graph.WIKIPEDIA, searchGraphConceptByLabelParams);

        final Matches matches = serviceCall.execute();

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

        ServiceCall<QueryConcepts> conceptsServiceCall = conceptInsights.conceptualSearch(corpus, parameters);

        // Synchronous call
        final QueryConcepts queryConcepts = conceptsServiceCall.execute();

        LOGGER.log(Level.INFO, "Found {0} matches for conceptual search", queryConcepts.getResults().size());

        return queryConcepts.getResults();
    }
}

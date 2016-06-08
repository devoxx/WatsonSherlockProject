package com.devoxx.watson.controller;

import com.devoxx.watson.model.AlchemyContent;
import com.devoxx.watson.service.AlchemyLanguageService;
import com.devoxx.watson.service.ConceptInsightsService;
import com.devoxx.watson.service.SpeechToTextException;
import com.devoxx.watson.service.SpeechToTextService;
import com.devoxx.watson.util.SoupUtil;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Document;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Documents;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;
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

    @Autowired
    SpeechToTextService speechToTextService;

    /**
     * Process an article or video link.
     *
     * @param link the article or presentation link
     * @throws DocumentAlreadyExistsException
     * @throws DocumentThumbnailKeywordsException
     * @return the alchemy content
     */
    AlchemyContent processLink(final String link)
            throws DocumentAlreadyExistsException,
                   DocumentThumbnailKeywordsException,
                   ArticleTextExtractionException {

        final AlchemyContent content = new AlchemyContent(link);

        final Document document = conceptInsightsService.findDocument(content.getId());

        if (document == null) {

            content.setThumbnail(SoupUtil.getThumbnail(link));

            content.setContent(alchemyLanguageService.getArticleText(link));

            alchemyLanguageService.process(content);

            conceptInsightsService.createDocument(content);

            return content;

        }

        // Does document have thumbnail keywords?
        // This piece of code was introduced for existing Watson documents which had no thumbnail processing
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

    /**
     * Process speech to text.
     *
     * @param audioFile the audio file
     * @param docName   the doc name
     * @return returns the transcript
     */
    public String processSpeechToText(final @NotNull File audioFile, final @NotNull String docName)
            throws SpeechToTextException {

        return speechToTextService.processAudioFile(audioFile, docName);
    }

    /**
     * Create an audio based Corpus document.
     *
     * @param docName the document name
     * @param link    the youtube link
     * @param transcript the audio transcript
     */
    public void createAudioDocument(final String docName,
                                    final String link,
                                    final String transcript,
                                    final String speakers) {

        final AlchemyContent content = new AlchemyContent(link);
        content.setAuthors(speakers);
        content.setTitle(docName);
        content.setContent(transcript);

        alchemyLanguageService.process(content);

        conceptInsightsService.createDocument(content);
    }
}

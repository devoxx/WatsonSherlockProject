package com.devoxx.watson.service;

import com.devoxx.watson.exception.ArticleTextExtractionException;
import com.devoxx.watson.model.AlchemyContent;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Concepts;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Keyword;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Keywords;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Stephan Janssen
 * @author James Weaver
 */
@Component
public class AlchemyLanguageService {

    private static final Logger LOGGER = Logger.getLogger(AlchemyLanguageService.class.getName());

    private static final String PUBLICATION_DATE = "publicationDate";
    private static final String LANGUAGE = "language";
    private static final String AUTHORS = "authors";
    private static final String DOC_SENTIMENT = "docSentiment";
    private static final String TITLE = "title";
    private static final String DOC_EMOTIONS = "docEmotions";

    // Alchemy Language REST endpoints
    private static final String URLGET_RANKED_IMAGE_KEYWORDS = "https://gateway-a.watsonplatform.net/calls/url/URLGetRankedImageKeywords";
    private static final String URLGET_TEXT = "https://gateway-a.watsonplatform.net/calls/url/URLGetText";
    private static final String URLGET_COMBINED_DATA = "https://gateway-a.watsonplatform.net/calls/url/URLGetCombinedData";
    private static final String TEXT_GET_RANKED_KEYWORDS = "https://gateway-a.watsonplatform.net/calls/text/TextGetRankedKeywords";

    // Alchemy Language REST parameters
    private static final String APIKEY = "apikey";
    private static final String URL = "url";
    private static final String TEXT = "text";
    private static final String OUTPUT_MODE = "outputMode";
    private static final String JSON = "json";
    private static final String KEYWORDS = "keywords";
    private static final String IMAGE_KEYWORDS = "imageKeywords";
    private static final String PUB_DATE = "date";
    private static final String DOC_SENTIMENT_TYPE = "type";

    private static final int TIMEOUT_IN_MILLIS = 15000;

    private String apikey;

    private AlchemyLanguage alchemyLanguage;

    @Autowired
    public void setAlchemyLanguage(final AlchemyLanguage alchemyLanguage) {
        this.alchemyLanguage = alchemyLanguage;
    }

    @Autowired
    public void setApikey(final String apikey) {
        this.apikey = apikey;
    }

    public void process(final AlchemyContent content) {

        final JsonObject json;
        try {

            // TODO execute alchemy on transcript instead of only article link
            json = getAlchemyData(content.getLink()).getAsJsonObject();

            if (content.getTitle() == null ||
                content.getTitle().isEmpty()) {
                content.setTitle(json.get(TITLE).getAsString());
            }

            if (content.getAuthors() == null ||
                content.getAuthors().isEmpty()) {
                content.setAuthors(json.get(AUTHORS).getAsJsonObject().get("names").getAsString());
            }

            if (json.has(PUBLICATION_DATE)) {
                final JsonObject pubDate = json.get(PUBLICATION_DATE).getAsJsonObject();
                if (pubDate.has(PUB_DATE)) {
                    content.setPublicationDate(pubDate.get(PUB_DATE).getAsString());
                }
            }

            if (json.has(LANGUAGE)) {
                content.setLanguage(json.get(LANGUAGE).getAsString());
            }

            if (json.has(DOC_SENTIMENT)) {
                final JsonObject docSentiment = json.get(DOC_SENTIMENT).getAsJsonObject();
                if (docSentiment.has(DOC_SENTIMENT_TYPE)) {
                    content.setSentiment(docSentiment.get(DOC_SENTIMENT_TYPE).getAsString());
                }
            }

            if (json.has(DOC_EMOTIONS)) {
                content.setEmotions(json.get(DOC_EMOTIONS).getAsJsonObject());
            }

            final String thumbnail = content.getThumbnail();
            if (thumbnail != null) {
                content.setThumbnailKeywords(getThumbnailKeywords(thumbnail));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *     curl -X POST \
     *          -d "apikey={API-KEY}" \
     *          -d "outputMode=json" \
     *          -d "extract=entities,keywords,authors, concepts, dates, doc-emotion, entities, feeds, keywords, pub-date, relations, doc-sentiment, taxonomy, title" \
     *          -d "sentiment=1" \
     *          -d "maxRetrieve=1" \
     *          -d "url=https://www.voxxed.com/blog/2016/01/microservices-versus-soa-practice/" \
     *          "https://gateway-a.watsonplatform.net/calls/url/URLGetCombinedData"
     */
    private JsonElement getAlchemyData(final String link) throws IOException {
        final Document doc =
                   Jsoup.connect(URLGET_COMBINED_DATA)
                        .timeout(TIMEOUT_IN_MILLIS)
                        .method(Connection.Method.POST)
                        .data(APIKEY, apikey)
                        .data(OUTPUT_MODE, JSON)
                        .data("extract", "authors, doc-emotion, pub-date, doc-sentiment, title")
                        .data(URL, link)
                        .ignoreContentType(true)
                        .execute()
                        .parse();

        return new JsonParser().parse(doc.text());
    }

    /**
     * Given the text of an abstract, identify keywords useful for recognizing
     *
     * @param text text of an abstract
     *
     * @return sorted list of unique keywords
     *
     *     curl -X POST \
     *          -d "apikey={API-KEY}" \
     *          -d "outputMode=json" \
     *          -d "text=this is some abstract text" \
     *          "https://gateway-a.watsonplatform.net/calls/text/TextGetRankedKeywords"
     */
    List<String> getKeywordsFromText(final String text) throws IOException {

        String abstractText = (text == null || text.length() == 0) ? "keyword" : text;
        final List<String> keywords = new ArrayList<>();
        final Document doc =
            Jsoup.connect(TEXT_GET_RANKED_KEYWORDS)
                .timeout(TIMEOUT_IN_MILLIS)
                .method(Connection.Method.POST)
                .data(APIKEY, apikey)
                .data(OUTPUT_MODE, JSON)
                .data(TEXT, abstractText)
                .ignoreContentType(true)
                .execute()
                .parse();

        final JsonElement element = new JsonParser().parse(doc.text());

        JsonArray array = element.getAsJsonObject().get(KEYWORDS).getAsJsonArray();

        for (final JsonElement keywordElement : array) {
            String label = keywordElement.getAsJsonObject().get("text").getAsString();
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

    /**
     * Same method as above but using directly the Java SDK (no doing any REST Call)
     * @param text the text to be parsed to find keywords
     * @return list of detected keywords
     */
    public List<String> getKeywordsFromTextAPI (final String text){
        Map<String, Object> params = new HashMap<>();
        params.put(AlchemyLanguage.TEXT,text);

        Keywords keywords = alchemyLanguage.getKeywords(params).execute();
        return keywords.getKeywords().stream()
                .map(Keyword::getText)
                .collect(Collectors.toList());
    }

    /**
     * curl -X POST \
     *      -d "apikey=$API_KEY" \
     *      -d "outputMode=json" \
     *      -d "url=http://techcrunch.com/2016/01/29/ibm-watson-weather-company-sale/" \
     *      "https://gateway-a.watsonplatform.net/calls/url/URLGetText"
     *
     * @param articleURL    the article link
     * @return the cleaned articled text
     */
    public String getArticleText(final String articleURL) throws ArticleTextExtractionException {
        final Document doc;
        try {
            doc = Jsoup.connect(URLGET_TEXT)
                    .timeout(TIMEOUT_IN_MILLIS)
                    .method(Connection.Method.POST)
                    .data(APIKEY, apikey)
                    .data(OUTPUT_MODE, JSON)
                    .data(URL, articleURL)
                    .ignoreContentType(true)
                    .execute()
                    .parse();
        } catch (IOException e) {
            throw new ArticleTextExtractionException(e.toString());
        }

        final JsonElement parse = new JsonParser().parse(doc.text());
        if (parse.getAsJsonObject().has("text")) {
            return parse.getAsJsonObject().get("text").getAsString();
        } else {
            return null;
        }
    }

    /**
     *
     * curl "https://gateway-a.watsonplatform.net/calls/url/URLGetRankedImageKeywords?url=http://www.edisonmuckers.org/fun-facts-about-tom&
     * outputMode=json&apikey=key"
     *
     * @param thumbnailURL the thumbnail URL
     * @return list of keyw
     * ords
     */
    public String getThumbnailKeywords(final String thumbnailURL) throws IOException {

        final Document doc = Jsoup.connect(URLGET_RANKED_IMAGE_KEYWORDS)
                                  .timeout(TIMEOUT_IN_MILLIS)
                                  .method(Connection.Method.GET)
                                  .data(APIKEY, apikey)
                                  .data(URL, thumbnailURL)
                                  .data(OUTPUT_MODE, JSON)
                                  .ignoreContentType(true)
                                  .execute()
                                  .parse();

        LOGGER.info(doc.text());

        final JsonObject json = new JsonParser().parse(doc.text()).getAsJsonObject();


        if (json.has(IMAGE_KEYWORDS)) {
            final JsonArray imageKeywords = json.get(IMAGE_KEYWORDS).getAsJsonArray();

            if (imageKeywords.size() > 0) {
                return imageKeywords.get(0).getAsJsonObject().get("text").getAsString();
            }
        }

        return "no results";
    }

    public Concepts getConcepts(final List<String> keywords){
        Map<String, Object> params = new HashMap<String, Object>();
        String taxonomyLabels = keywords.stream().collect(Collectors.joining(" and "));
        params.put(AlchemyLanguage.CQUERY,taxonomyLabels);

        return alchemyLanguage.getConcepts(params).execute();

    }


}

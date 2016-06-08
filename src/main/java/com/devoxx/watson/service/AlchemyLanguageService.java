package com.devoxx.watson.service;

import com.devoxx.watson.controller.ArticleTextExtractionException;
import com.devoxx.watson.model.AlchemyContent;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Stephan Janssen
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

    // Alchemy Language REST parameters
    private static final String APIKEY = "apikey";
    private static final String URL = "url";
    private static final String OUTPUT_MODE = "outputMode";
    private static final String JSON = "json";
    private static final String IMAGE_KEYWORDS = "imageKeywords";
    private static final String PUB_DATE = "date";
    private static final String DOC_SENTIMENT_TYPE = "type";

    private String apikey;

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
                        .timeout(15000)
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
                    .timeout(15000)
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
                                  .timeout(15000)
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
}

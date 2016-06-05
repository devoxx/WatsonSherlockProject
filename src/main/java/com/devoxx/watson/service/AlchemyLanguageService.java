package com.devoxx.watson.service;

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

    private String apikey;

    @Autowired
    public void setApikey(final String apikey) {
        this.apikey = apikey;
    }

    public void process(final AlchemyContent content) {

        try {
            final String articleText = getArticleText(content.getLink());

            if (articleText != null && !articleText.isEmpty()) {
                content.setContent(articleText);

                final JsonObject json = getAlchemyData(content.getLink()).getAsJsonObject();

                content.setTitle(json.get(TITLE).getAsString());

                content.setPublicationDate(json.get(PUBLICATION_DATE).getAsJsonObject().get("date").getAsString());

                content.setLanguage(json.get(LANGUAGE).getAsString());

                content.setAuthors(json.get(AUTHORS).getAsJsonObject().get("names").getAsString());

                content.setSentiment(json.get(DOC_SENTIMENT).getAsJsonObject().get("type").getAsString());

                content.setEmotions(json.get(DOC_EMOTIONS).getAsJsonObject());

                content.setThumbnailKeywords(getThumbnailKeywords(content.getThumbnail()));
            }
        } catch (IOException e) {
            LOGGER.severe(e.toString());
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
    private JsonElement getAlchemyData(final String articleURL) throws IOException {
        final Document doc =
                   Jsoup.connect(URLGET_COMBINED_DATA)
                        .timeout(15000)
                        .method(Connection.Method.POST)
                        .data(APIKEY, apikey)
                        .data(OUTPUT_MODE, JSON)
                        .data("extract", "authors, doc-emotion, pub-date, doc-sentiment, title")
                        .data(URL, articleURL)
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
    private String getArticleText(final String articleURL) throws IOException {
        final Document doc =
                Jsoup.connect(URLGET_TEXT)
                        .timeout(15000)
                        .method(Connection.Method.POST)
                        .data(APIKEY, apikey)
                        .data(OUTPUT_MODE, JSON)
                        .data(URL, articleURL)
                        .ignoreContentType(true)
                        .execute()
                        .parse();

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

        final JsonElement element = new JsonParser().parse(doc.text());

        final JsonArray imageKeywords = element.getAsJsonObject().get("imageKeywords").getAsJsonArray();

        if (imageKeywords.size() > 0) {
            return imageKeywords.get(0).getAsJsonObject().get("text").getAsString();
        } else {
            return "no results";
        }
    }
}

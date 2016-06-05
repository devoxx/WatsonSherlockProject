package com.devoxx.watson.service;

import com.devoxx.watson.model.AlchemyContent;
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

    private String apikey;

    @Autowired
    public void setApikey(final String apikey) {
        this.apikey = apikey;
    }

    public void process(final AlchemyContent alchemyContent) {

        try {
            final String articleText = getArticleText(alchemyContent.getLink());

            if (articleText != null) {
                alchemyContent.setContent(articleText);

                JsonElement jsonElement = getAlchemyData(alchemyContent.getLink());

                final JsonObject jsonObject = jsonElement.getAsJsonObject();

                alchemyContent.setTitle(jsonObject.get(TITLE).getAsString());

                alchemyContent.setPublicationDate(jsonObject.get(PUBLICATION_DATE).getAsJsonObject().get("date").getAsString());

                alchemyContent.setLanguage(jsonObject.get(LANGUAGE).getAsString());

                alchemyContent.setAuthors(jsonObject.get(AUTHORS).getAsJsonObject().get("names").getAsString());

                alchemyContent.setSentiment(jsonObject.get(DOC_SENTIMENT).getAsJsonObject().get("type").getAsString());

                alchemyContent.setEmotions(jsonObject.get(DOC_EMOTIONS).getAsJsonObject());
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
                   Jsoup.connect("https://gateway-a.watsonplatform.net/calls/url/URLGetCombinedData")
                        .timeout(15000)
                        .method(Connection.Method.POST)
                        .data("apikey", apikey)
                        .data("outputMode", "json")
                        .data("extract", "authors, doc-emotion, pub-date, doc-sentiment, title")
                        .data("url", articleURL)
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
                Jsoup.connect("https://gateway-a.watsonplatform.net/calls/url/URLGetText")
                        .timeout(15000)
                        .method(Connection.Method.POST)
                        .data("apikey", apikey)
                        .data("outputMode", "json")
                        .data("url", articleURL)
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
}

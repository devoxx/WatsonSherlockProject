package com.devoxx.watson.controller;

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

/**
 * @author Stephan Janssen
 */
@Component
public class AlchemyAPIService {


    private static final String PUBLICATION_DATE = "publicationDate";
    private static final String LANGUAGE = "language";
    private static final String AUTHORS = "authors";
    private static final String DOC_SENTIMENT = "docSentiment";
    private static final String TITLE = "title";
    private String apikey;

    @Autowired
    public void setApikey(final String apikey) {
        this.apikey = apikey;
    }

    AlchemyContent process(final String articleURL) {

        final AlchemyContent alchemyContent = new AlchemyContent();
        alchemyContent.setLink(articleURL);

        try {

            JsonElement jsonElement = getAlchemyData(articleURL);

            final String articleText = getArticleText(articleURL);
            alchemyContent.setContent(articleText);

            createAlchemyContent(jsonElement, alchemyContent);

        } catch (IOException e) {
            return null;
        }

        return alchemyContent;
    }

    private void createAlchemyContent(final JsonElement jsonElement,
                                                final AlchemyContent alchemyContent) {

        final JsonObject jsonObject = jsonElement.getAsJsonObject();

        alchemyContent.setTitle(jsonObject.get(TITLE).getAsString());

        alchemyContent.setPublicationDate(jsonObject.get(PUBLICATION_DATE).getAsJsonObject().get("date").getAsString());

        alchemyContent.setLanguage(jsonObject.get(LANGUAGE).getAsString());

        alchemyContent.setAuthors(jsonObject.get(AUTHORS).getAsJsonObject().get("names").getAsString());

        alchemyContent.setSentiment(jsonObject.get(DOC_SENTIMENT).getAsJsonObject().get("type").getAsString());

        alchemyContent.setEmotions(jsonObject.get("docEmotions").getAsJsonObject());
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
                        .data("extract", "entities,keywords,authors, concepts, dates, doc-emotion, entities, feeds, keywords, pub-date, relations, doc-sentiment, taxonomy, title")
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

        return new JsonParser().parse(doc.text()).getAsJsonObject().get("text").getAsString();
    }
}

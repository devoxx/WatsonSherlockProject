package com.devoxx.watson.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Optional;

/**
 * @author Stephan Janssen
 */
public class SoupUtil {

    // Accelerated Mobile Pages
    private static final String AMP = "?amp";

    private static final int TEN_SECONDS_IN_MILLIS = 10000;

    private static Document getDocument(String link, final boolean returnAMP) {

        if (returnAMP && !link.endsWith(AMP)) {
            link += AMP;
        }

        final Connection connect = Jsoup.connect(link);
        connect.timeout(TEN_SECONDS_IN_MILLIS);

        Document doc = null;
        try {
            doc = connect.get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    public static String getThumbnail(final String hyperLink) {

        // Retrieve thumbnail for document from twitter or facebook HTML card
        final Document htmlDocument = SoupUtil.getDocument(hyperLink, false);
        Optional<Element> imageDoc = htmlDocument.select("meta").stream().filter(e -> e.toString().contains("twitter:image")).findFirst();
        if (!imageDoc.isPresent()) {
            imageDoc = htmlDocument.select("meta").stream().filter(e -> e.toString().contains("og:img")).findFirst();
        }

        // If thumbnail remains empty then default Devoxx image is used.
        String thumbnail = "";
        if (imageDoc.isPresent()) {
            thumbnail = imageDoc.get().attr("content");
        }

        return thumbnail;
    }
}

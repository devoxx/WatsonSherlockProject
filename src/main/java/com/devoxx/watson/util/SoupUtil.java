package com.devoxx.watson.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * @author Stephan Janssen
 */
public class SoupUtil {

    // Accelerated Mobile Pages
    private static final String AMP = "?amp";

    private static final int TEN_SECONDS_IN_MILLIS = 10000;

    public static Document getDocument(String link) {

        if (!link.endsWith(AMP)) {
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
}

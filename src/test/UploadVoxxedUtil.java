import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author Stephan Janssen
 */
public class UploadVoxxedUtil {



    private UploadVoxxedUtil() {


        try (Stream<String> stream = Files.lines(Paths.get("/Users/stephan/Downloads/links.txt"))) {

            stream.forEach(string -> {
                try {
                    sendPost(string);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // HTTP POST request
    private void sendPost(String link) throws Exception {

        String newLink = link.substring(1, link.indexOf("\""));

        final Connection.Response response =
                   Jsoup.connect("http://localhost:8080/api/article/")
                        .timeout(15000)
                        .method(Connection.Method.POST)
                        .data("link", newLink)
                        .execute();

        System.out.println(newLink + " - " +response.statusMessage());
    }

    public static void main(String args[]) {
        new UploadVoxxedUtil();
    }
}

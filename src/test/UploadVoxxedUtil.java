import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.springframework.http.HttpHeaders.USER_AGENT;

/**
 * @author Stephan Janssen
 */
public class UploadVoxxedUtil {



    private UploadVoxxedUtil() {


        try (Stream<String> stream = Files.lines(Paths.get("/Users/stephan/Downloads/links.txt"))) {

            stream.forEach(string -> {
                try {
                    sendPost(string);
                    Thread.sleep(2000);
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

        String url = "http://localhost:8080/api/article/";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "link="+ newLink;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

    }

    public static void main(String args[]) {
        new UploadVoxxedUtil();
    }
}

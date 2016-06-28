
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.stream.StreamSupport;

/**
 * YouTube processor has the following actions:
 *
 * DOWNLOAD :  Downloads the YouTube audio files for a given channel ID using the youtube-dl command
 *
 * FFMPEG : Prints the FFMPEG commands to convert an audio file to OGG
 *
 * @link https://rg3.github.io/youtube-dl/download.html
 *
 * @author Stephan Janssen
 */
public class YouTubeProcessor {

    private static final String MAX_RESULTS = "25";
    private static final String RESULT_TYPE = "video";
    private static final String RESULT_ORDER = "viewCount";
    private static final String DEVOXX_BE_2015_CHANNEL_ID = "UCCBVCTuk6uJrN3iFV_3vurg";
    private static final String DOWNLOADS = "./downloads";

    private File downloadsPath = new File(DOWNLOADS);

    @SuppressWarnings("unused")
    private enum Action {

        // Downloads the audio files for the given YouTube channel
        DOWNLOAD,

        // Print the FFMPEG commands for OGG conversion
        FFMPEG
    }

    private YouTubeProcessor(final String apiKey, final Action action) {

        switch (action) {

            case DOWNLOAD : downloadTalks(apiKey);
                break;

            default : convertAudioFilesToOGG();

        }
    }

    /**
     *
     */
    private void downloadTalks(final String apiKey) {

        // Download audio files
        try {
            JsonArray videos = getVideos(apiKey);

            StreamSupport.stream(videos.spliterator(), false)
                    .forEach(this::downloadAudioFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadAudioFile(final JsonElement video) {

        final String videoId = ((JsonObject)video).get("id").getAsJsonObject().get("videoId").getAsString();

        if (audioFileExist(videoId)) {
            return;
        }

        // -x downloads only the available audio file
        CommandLine cmdLine = CommandLine.parse("youtube-dl -x " + videoId);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(1);
        executor.setWorkingDirectory(downloadsPath);
        try {
            executor.execute(cmdLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean audioFileExist(String videoId) {

        if (!downloadsPath.exists()) {
            downloadsPath.mkdir();
            return false;
        }

        File[] files = downloadsPath.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getAbsolutePath().contains(videoId)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Prints the FFMPEG commands to convert an m4a or webm audio file to OGG.
     *
     * Make sure to install the libvorbis codec using : brew reinstall ffmpeg --with-libvorbis
     *
     * How to install FFMPEG on Mac?
     * @link https://trac.ffmpeg.org/wiki/CompilationGuide/MacOSX
     */
    private void convertAudioFilesToOGG() {

        File[] files = downloadsPath.listFiles();
        if (files == null) {
            System.out.println("No downloaded audio files found.");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {

                String ffmpeg = "unknown conversion for " + file.getName();

                if (file.getAbsolutePath().endsWith(".m4a")) {

                    // ffmpeg -i audio.m4a -acodec vorbis -aq 60 -vn -ac 2 -strict experimental output.ogg
                    ffmpeg = String.format("ffmpeg -i \"%s\" -acodec vorbis -aq 60 -vn -ac 2 -strict experimental \"%s.ogg\"", file.getName(), file.getName());

                } else if (file.getAbsolutePath().endsWith(".webm")) {

                    // ffmpeg -i  "Interview with Pepper robot-A-2IVBIpnng.m4a.ogg" -vn -acodec copy ./output.ogg
                    ffmpeg = String.format("ffmpeg -i \"%s\" -vn -acodec libvorbis \"%s.ogg\"", file.getName(), file.getName());

                }
                if (!file.getAbsolutePath().endsWith("ogg")) {
                    System.out.println(ffmpeg);
                }
            }
        }
    }

    /**
     * Returns the videos for the selected channel ID.
     *
     * curl "https://www.googleapis.com/youtube/v3/search?
     * key=API-KEY
     * channelId=UCCBVCTuk6uJrN3iFV_3vurg&
     * part=snippet,id&
     * maxResults=20&
     * type=video&
     * order=viewCount"
     *
     * @param apiKey    The YouTube channel API KEY
     * @return a JSON array of found videos
     */
    private JsonArray getVideos(final String apiKey) throws IOException {

        final Document document = Jsoup.connect("https://www.googleapis.com/youtube/v3/search")
                .timeout(15000)
                .method(Connection.Method.GET)
                .data("key", apiKey)
                .data("channelId", DEVOXX_BE_2015_CHANNEL_ID)
                .data("part", "snippet,id")
                .data("maxResults", MAX_RESULTS)
                .data("type", RESULT_TYPE)
                .data("order", RESULT_ORDER)
                .ignoreContentType(true)
                .execute()
                .parse();

        return (JsonArray) new JsonParser().parse(document.text()).getAsJsonObject().get("items");
    }

    public static void main(String args[]) {

        System.out.println("We assume you've youtube-dl installed, install details @ https://rg3.github.io/youtube-dl/download.html ");

        if (args.length != 1) {
            System.out.println("Usage: YouTubeProcess YOUTUBE-API-KEY");
            System.exit(-1);
        }

        new YouTubeProcessor(args[0], Action.DOWNLOAD);
    }
}
package com.devoxx.watson.controller;

import com.devoxx.watson.configuration.DevoxxWatsonInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Stephan Janssen
 */
@Component
class ProcessAudioFileTimer {

    private static final String OGG_FILE_EXTENSION = ".ogg";

    @Autowired
    private ProcessAudioFile processAudioFile;

    private static final Logger LOGGER = Logger.getLogger(ProcessAudioFileTimer.class.getName());

    // Scan every 15 seconds
    @Scheduled(fixedDelay = 15000L)
    public void scanUploadDirectoryForNewAudioFiles() {

        File folder = new File(DevoxxWatsonInitializer.LOCATION);

        final FilenameFilter filenameFilter = (dir, name) -> {
            if (name.lastIndexOf(".") > 0) {

                // get extension
                String str = name.substring(name.lastIndexOf('.'));

                // match path name extension
                if (str.equals(OGG_FILE_EXTENSION)) {
                    return true;
                }
            }
            return false;
        };

        File[] listOfFiles = folder.listFiles(filenameFilter);
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                // process file
                LOGGER.log(Level.FINE, "Processing {0}", file.getName());

                List<String> content = getMetaFileContentAndRemove(file);

                if (!content.isEmpty()) {
                    processAudioFile.execute(file, content.get(1), content.get(0), content.get(2));
                } else {
                    LOGGER.log(Level.FINER, "Already processed!");
                }
            }
        }
    }

    /**
     * Read the meta content for audio file and remove it to avoid duplicate processing.
     *
     * @param file  the meta content file
     * @return meta content
     */
    private List<String> getMetaFileContentAndRemove(final File file) {
        // Create the meta data txt file with YouTube link
        String txtFileName = file.getAbsolutePath()+".txt";
        File txtFile = new File(txtFileName);
        List<String> content = new ArrayList<>();

        if (txtFile.exists()) {
            // Read meta data file
            try {
                content = Files.readAllLines(txtFile.toPath());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getCause().toString());
            }

            // Delete meta file to avoid processing again!
            txtFile.delete();
        }
        return content;
    }
}

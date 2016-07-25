package com.devoxx.watson.controller;

import com.devoxx.watson.configuration.DevoxxWatsonInitializer;
import com.devoxx.watson.exception.FileException;
import com.devoxx.watson.model.SpeechToTextModel;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Concepts;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentsResult;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by danieldeluca on 22/07/16.
 */
@RequestMapping("/api/robot")
@RestController
public class RobotRestController {
    private static final Logger LOGGER = Logger.getLogger(RobotRestController.class.getName());

    @Autowired
    private WatsonController watsonController;


    private File storeTmpFile(MultipartFile file) throws FileException {
        // check if all form parameters are provided
        if (file == null)
            throw new FileException("MultipartFile File Required");
        // create our destination folder, if it not exists

        File uploadPath = new File(DevoxxWatsonInitializer.LOCATION);

        // Now do something with file...
        File tmpFile;
        try {
            tmpFile = File.createTempFile("devoxx-speechtext", UUID.randomUUID().toString()+".ogg",uploadPath);
            LOGGER.log(Level.INFO,tmpFile.getAbsolutePath());
            FileOutputStream tempFileOutputStream = new FileOutputStream(tmpFile);
            FileCopyUtils.copy(file.getBytes(), tempFileOutputStream);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Can not save file tmpFile",e);
            throw new FileException("Can not save file tmpFile",e);
        }
        return tmpFile;
    }

    private List<SpeechToTextModel> getSpeechToTextModels(MultipartFile file) throws FileException {
        File tmpFile = storeTmpFile(file);
        List<SpeechToTextModel> analysisResults = watsonController.speechToText(tmpFile);
        if (! tmpFile.delete()){
            LOGGER.log(Level.SEVERE, "Can't Delete File:{0}:", tmpFile.getAbsolutePath());
        }
        return analysisResults;
    }

    /**
     * Analyse the OGG audio file and return the detected sentence and corresponding confidence
     * @param file the OGG audio file
     * @return the detected sentence and analysis confidence
     */
    @RequestMapping(value = "/speechtotext"
            , method = RequestMethod.POST
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity speechToText(@RequestParam("file") MultipartFile file){
        List<SpeechToTextModel> analysisResults;
        try {
            analysisResults = getSpeechToTextModels(file);
        } catch (FileException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(analysisResults, HttpStatus.OK);
    }

    /**
     * Analyse the OGG audio file to determine the sentence and return sentence's keywords
     * @param file the OGG audio file
     * @return the sentence keywords or empty of confidence is lower than application property: speechtotext.min.confidence
     */
    @RequestMapping(value = "/keywords"
            , method = RequestMethod.POST
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity speechToKeywords(@RequestParam("file") MultipartFile file){
        List<SpeechToTextModel> analysisResults;
        try {
            analysisResults = getSpeechToTextModels(file);
        } catch (FileException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<String> keywordsFromTextModels =  watsonController.getKeywordsFromTextModels(analysisResults);
        return new ResponseEntity<>(keywordsFromTextModels, HttpStatus.OK);
    }

    @RequestMapping(value="/concepts"
            , method = RequestMethod.POST
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getConcepts(@RequestParam("keywords") List<String> keywordList){
        if (keywordList.isEmpty()){
            return new ResponseEntity<>("Missing keywords", HttpStatus.NOT_ACCEPTABLE);
        }
        System.out.println("keywordList:"+keywordList.toString()+":");
        List<Result> results =  watsonController.getDocumentsNews(keywordList);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}


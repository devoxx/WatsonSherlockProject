package com.devoxx.watson.controller;

import com.devoxx.watson.configuration.DevoxxWatsonInitializer;
import com.devoxx.watson.model.FileBucket;
import com.devoxx.watson.util.FileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class FileUploadController {

    private static final Logger LOGGER = Logger.getLogger(FileUploadController.class.getName());

    @Autowired
    FileValidator fileValidator;

    @InitBinder("fileBucket")
    protected void initBinderFileBucket(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping(value = {"/", "/welcome"}, method = RequestMethod.GET)
    public String getHomePage(ModelMap model) {
        return "welcome";
    }

    @RequestMapping(value = "/audioFileUploader", method = RequestMethod.GET)
    public String getSingleUploadPage(ModelMap model) {
        FileBucket fileModel = new FileBucket();
        model.addAttribute("fileBucket", fileModel);
        return "audioFileUploader";
    }

    @RequestMapping(value = "/audioFileUploader", method = RequestMethod.POST)
    public String audioFileUpload(@Valid FileBucket fileBucket,
                                   BindingResult result, ModelMap model) throws IOException {

        if (result.hasErrors()) {
            LOGGER.log(Level.FINE, "Form validation errors");
            return "audioFileUploader";
        } else {
            LOGGER.log(Level.FINE, "Fetching audio file");
            MultipartFile multipartFile = fileBucket.getFile();

            File uploadPath = new File(DevoxxWatsonInitializer.LOCATION);

            // Now do something with file...
            File tempFile = File.createTempFile("devoxx",
                    fileBucket.getFile().getOriginalFilename(),
                    uploadPath);

            FileCopyUtils.copy(fileBucket.getFile().getBytes(), tempFile);
            String fileName = multipartFile.getOriginalFilename();
            model.addAttribute("fileName", fileName);

            // Create the meta data txt file with YouTube link
            String txtFileName = tempFile.getAbsolutePath()+".txt";
            File txtFile = new File(txtFileName);
            PrintWriter writer = new PrintWriter(txtFile, "UTF-8");
            writer.println(fileBucket.getLink());
            writer.println(fileBucket.getDocName());
            writer.close();

            return "success";
        }
    }
}
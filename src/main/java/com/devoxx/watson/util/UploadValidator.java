package com.devoxx.watson.util;

import com.devoxx.watson.model.Article;
import com.devoxx.watson.model.FileBucket;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Stephan Janssen
 */
@Component
public class UploadValidator implements Validator {

    public boolean supports(Class<?> clazz) {
        return FileBucket.class.isAssignableFrom(clazz) || Article.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        if (obj instanceof FileBucket) {
            FileBucket file = (FileBucket) obj;

            if (file.getFile() != null) {
                if (file.getFile().getSize() == 0) {
                    errors.rejectValue("file", "missing.file");
                } else if (!file.getFile().getOriginalFilename().endsWith(".ogg")) {
                    errors.rejectValue("file", "wrong.audiofile");
                } else if (file.getDocName().isEmpty()) {
                    errors.rejectValue("docName", "missing.docname");
                } else if (file.getLink().isEmpty()) {
                    errors.rejectValue("link", "missing.link");
                }
            }
        } else {
            Article article = (Article) obj;
            String link = article.getLink();
            if (link != null) {
                if (link.isEmpty()) {
                    errors.rejectValue("link", "missing.articlelink");
                } else if (link.toLowerCase().startsWith("https://www.voxxed.com")) {
                    errors.rejectValue("link", "hostname.wrong");
                }

                // Check if the link is a valid URL
                try {
                    new URL(link);
                } catch (MalformedURLException e) {
                    errors.rejectValue("link", "malformed.link");
                }
            }
        }
    }
}

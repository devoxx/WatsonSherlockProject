package com.devoxx.watson.util;

import com.devoxx.watson.model.FileBucket;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class FileValidator implements Validator {

    public boolean supports(Class<?> clazz) {
        return FileBucket.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
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
    }
}
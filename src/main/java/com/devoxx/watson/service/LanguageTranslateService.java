package com.devoxx.watson.service;

import com.ibm.watson.developer_cloud.language_translation.v2.LanguageTranslation;
import com.ibm.watson.developer_cloud.language_translation.v2.model.Language;
import com.ibm.watson.developer_cloud.language_translation.v2.model.TranslationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Stephan Janssen
 */
@Component
public class LanguageTranslateService {

    private LanguageTranslation languageTranslation;

    @Autowired
    public void setLanguageTranslation(final LanguageTranslation languageTranslation) {
        this.languageTranslation = languageTranslation;
    }

    public String translate(final String text, final Language source, final Language target) {

        final TranslationResult execute = languageTranslation.translate(text, source, target).execute();

        return execute.getFirstTranslation();
    }
}

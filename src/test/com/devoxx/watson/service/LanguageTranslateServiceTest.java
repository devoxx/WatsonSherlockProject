package com.devoxx.watson.service;

import com.ibm.watson.developer_cloud.language_translation.v2.LanguageTranslation;
import com.ibm.watson.developer_cloud.language_translation.v2.model.Language;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * @author Stephan Janssen
 */
public class LanguageTranslateServiceTest {

    // This is an integration test
    @Test
    public void translateEnglishToFrenchTest() {

        final LanguageTranslation languageTranslation = new LanguageTranslation();
        languageTranslation.setUsernameAndPassword("ae793894-f77b-4ed5-b76f-30247d1b6edb", "G5KQ65F7wo6p");

        final LanguageTranslateService languageTranslateService = new LanguageTranslateService();
        languageTranslateService.setLanguageTranslation(languageTranslation);

        final String translate = languageTranslateService.translate("This is a test", Language.ENGLISH, Language.FRENCH);

        Assert.notNull(translate);
        Assert.isTrue("C'est un test".equalsIgnoreCase(translate));
    }

    // This is an integration test
    @Test
    public void translateFrenchToEnglishTest() {

        final LanguageTranslation languageTranslation = new LanguageTranslation();
        languageTranslation.setUsernameAndPassword("ae793894-f77b-4ed5-b76f-30247d1b6edb", "G5KQ65F7wo6p");

        final LanguageTranslateService languageTranslateService = new LanguageTranslateService();
        languageTranslateService.setLanguageTranslation(languageTranslation);

        final String translate = languageTranslateService.translate("C'est un test", Language.FRENCH, Language.ENGLISH);

        Assert.notNull(translate);
        Assert.isTrue("it is a test".equalsIgnoreCase(translate));
    }

}

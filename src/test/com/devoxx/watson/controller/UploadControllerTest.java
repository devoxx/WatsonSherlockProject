package com.devoxx.watson.controller;

import com.devoxx.watson.model.Article;
import org.junit.Test;
import org.springframework.validation.BindingResult;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Stephan Janssen
 */
public class UploadControllerTest {

    @Test
    public void processAMPLink() {
        final UploadController fileUploadController = new UploadController();

        final Article article = new Article();
        article.setLink("https://www.voxxed.com/blog/2016/05/j-unit-5-extension-model/");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        final String output = fileUploadController.contentUpload(article, bindingResult, null);

        assert(output.equals("success"));
    }
}

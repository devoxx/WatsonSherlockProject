package com.devoxx.watson.configuration;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

public class DevoxxWatsonInitializer extends
        AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{DevoxxWatsonConfiguration.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setMultipartConfig(getMultipartConfigElement());
    }

    private MultipartConfigElement getMultipartConfigElement() {

        return new MultipartConfigElement(LOCATION, MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD);
    }

    // Temporary location where files will be stored
    public static final String LOCATION = "./";

    // 75MB : Max file size.
    private static final long MAX_FILE_SIZE = 78643200;

    // Beyond that size spring will throw exception.
    // 75MB : Total request size containing Multi part.
    private static final long MAX_REQUEST_SIZE = 78643200;

    // Size threshold after which files will be written to disk
    private static final int FILE_SIZE_THRESHOLD = 0;
}
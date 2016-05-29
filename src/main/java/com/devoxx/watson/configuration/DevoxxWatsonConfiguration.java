package com.devoxx.watson.configuration;

import com.ibm.watson.developer_cloud.concept_insights.v2.ConceptInsights;
import com.ibm.watson.developer_cloud.concept_insights.v2.model.Corpus;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;

@Configuration
@EnableAsync
@EnableScheduling
@EnableWebMvc
@EnableSwagger2
@PropertySource(value = { "classpath:application.properties" })
@ComponentScan(basePackages = "com.devoxx.watson")
public class DevoxxWatsonConfiguration extends WebMvcConfigurerAdapter {

    @Resource
    private Environment env;

    @Bean(name = "multipartResolver")
    public StandardServletMultipartResolver resolver() {
        return new StandardServletMultipartResolver();
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        registry.viewResolver(viewResolver);
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations(
                "/static/");
        registry.addResourceHandler("/swagger/**")
                .addResourceLocations("/swagger/");
    }

    @Bean
    public SpeechToText speechToText() {
        SpeechToText speechToText = new SpeechToText();
        String username = env.getProperty("speech.username");
        String password = env.getProperty("speech.password");
        speechToText.setUsernameAndPassword(username, password);
        return speechToText;
    }

    @Bean
    public ConceptInsights conceptInsights() {
        final ConceptInsights conceptInsights = new ConceptInsights();
        String username = env.getProperty("insight.username");
        String password = env.getProperty("insight.password");
        conceptInsights.setUsernameAndPassword(username, password);

        return conceptInsights;
    }

    @Bean
    public Corpus getCorpus() {
        final ConceptInsights conceptInsights = conceptInsights();
        return new Corpus(conceptInsights.getFirstAccountId(), env.getProperty("corpus.name"));
    }
}
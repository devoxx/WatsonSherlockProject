package com.devoxx.watson.service;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyDataNews;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentsResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by danieldeluca on 24/07/16.
 */
@Component
public class AlchemyDataNewsService {
    private static final Logger LOGGER = Logger.getLogger(AlchemyDataNewsService.class.getName());

    private AlchemyDataNews alchemyDataNews;
    private Integer alchemyDataNewsServiceYearsInThePast;

    @Autowired
    public void setAlchemyDataNews(final AlchemyDataNews alchemyDataNews) {
        this.alchemyDataNews = alchemyDataNews;
    }

    @Autowired
    public void setAlchemyDataNewsServiceYearsInThePast(final Integer alchemyDataNewsServiceYearsInThePast) {
        this.alchemyDataNewsServiceYearsInThePast = alchemyDataNewsServiceYearsInThePast;
    }

    public DocumentsResult getDocuments(final List<String> keywords){
        Map<String, Object> params = new HashMap<String, Object>();

        String[] fields =
                new String[] {"enriched.url.title", "enriched.url.url", "enriched.url.author",
                        "enriched.url.publicationDate", "enriched.url.enrichedTitle.entities",
                        "enriched.url.enrichedTitle.docSentiment"};

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startDate = today.minus(alchemyDataNewsServiceYearsInThePast, ChronoUnit.YEARS);
        ZoneId zoneId = ZoneId.systemDefault();
        params.put(AlchemyDataNews.RETURN, StringUtils.join(fields, ","));
        params.put(AlchemyDataNews.START, String.valueOf(startDate.atZone(zoneId).toEpochSecond()));
        params.put(AlchemyDataNews.END, String.valueOf(today.atZone(zoneId).toEpochSecond()));
        params.put(AlchemyDataNews.COUNT, 10);

        //Query on adjacent nested fields:
        //params.put("q.enriched.url.enrichedTitle.entities.entity", "|text=IBM,type=company|");
        //params.put("q.enriched.url.enrichedTitle.docSentiment.type", "positive");

        String taxonomyLabels = keywords.stream().collect(Collectors.joining(" and "));
        System.out.println("TaxonomyLabels:"+taxonomyLabels+":");
        params.put("q.enriched.url.enrichedTitle.taxonomy.taxonomy_.label",taxonomyLabels);

        return alchemyDataNews.getNewsDocuments(params).execute();

    }

}

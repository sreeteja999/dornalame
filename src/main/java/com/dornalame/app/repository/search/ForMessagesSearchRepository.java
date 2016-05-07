package com.dornalame.app.repository.search;

import com.dornalame.app.domain.ForMessages;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ForMessages entity.
 */
public interface ForMessagesSearchRepository extends ElasticsearchRepository<ForMessages, Long> {
}

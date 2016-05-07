package com.dornalame.app.repository.search;

import com.dornalame.app.domain.Reference;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Reference entity.
 */
public interface ReferenceSearchRepository extends ElasticsearchRepository<Reference, Long> {
}

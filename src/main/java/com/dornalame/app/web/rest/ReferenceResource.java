package com.dornalame.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.dornalame.app.domain.Reference;
import com.dornalame.app.repository.ReferenceRepository;
import com.dornalame.app.repository.search.ReferenceSearchRepository;
import com.dornalame.app.web.rest.util.HeaderUtil;
import com.dornalame.app.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Reference.
 */
@RestController
@RequestMapping("/public")
public class ReferenceResource {

    private final Logger log = LoggerFactory.getLogger(ReferenceResource.class);

    @Inject
    private ReferenceRepository referenceRepository;

    @Inject
    private ReferenceSearchRepository referenceSearchRepository;

    /**
     * POST  /references : Create a new reference.
     *
     * @param reference the reference to create
     * @return the ResponseEntity with status 201 (Created) and with body the new reference, or with status 400 (Bad Request) if the reference has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/references",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Reference> createReference(@Valid @RequestBody Reference reference) throws URISyntaxException {
        log.debug("REST request to save Reference : {}", reference);
        if (reference.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("reference", "idexists", "A new reference cannot already have an ID")).body(null);
        }
        Reference result = referenceRepository.save(reference);
        referenceSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/references/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("reference", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /references : Updates an existing reference.
     *
     * @param reference the reference to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated reference,
     * or with status 400 (Bad Request) if the reference is not valid,
     * or with status 500 (Internal Server Error) if the reference couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/references",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Reference> updateReference(@Valid @RequestBody Reference reference) throws URISyntaxException {
        log.debug("REST request to update Reference : {}", reference);
        if (reference.getId() == null) {
            return createReference(reference);
        }
        Reference result = referenceRepository.save(reference);
        referenceSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("reference", reference.getId().toString()))
            .body(result);
    }

    /**
     * GET  /references : get all the references.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of references in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/references",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Reference>> getAllReferences(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of References");
        Page<Reference> page = referenceRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/references");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /references/:id : get the "id" reference.
     *
     * @param id the id of the reference to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the reference, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/references/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Reference> getReference(@PathVariable Long id) {
        log.debug("REST request to get Reference : {}", id);
        Reference reference = referenceRepository.findOne(id);
        return Optional.ofNullable(reference)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /references/:id : delete the "id" reference.
     *
     * @param id the id of the reference to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/references/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteReference(@PathVariable Long id) {
        log.debug("REST request to delete Reference : {}", id);
        referenceRepository.delete(id);
        referenceSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("reference", id.toString())).build();
    }

    /**
     * SEARCH  /_search/references?query=:query : search for the reference corresponding
     * to the query.
     *
     * @param query the query of the reference search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/references",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Reference>> searchReferences(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of References for query {}", query);
        Page<Reference> page = referenceSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/references");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

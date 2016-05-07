package com.dornalame.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.dornalame.app.domain.ForMessages;
import com.dornalame.app.repository.ForMessagesRepository;
import com.dornalame.app.repository.search.ForMessagesSearchRepository;
import com.dornalame.app.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ForMessages.
 */
@RestController
@RequestMapping("/public")
public class ForMessagesResource {

    private final Logger log = LoggerFactory.getLogger(ForMessagesResource.class);

    @Inject
    private ForMessagesRepository forMessagesRepository;

    @Inject
    private ForMessagesSearchRepository forMessagesSearchRepository;

    /**
     * POST  /for-messages : Create a new forMessages.
     *
     * @param forMessages the forMessages to create
     * @return the ResponseEntity with status 201 (Created) and with body the new forMessages, or with status 400 (Bad Request) if the forMessages has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/for-messages",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ForMessages> createForMessages(@RequestBody ForMessages forMessages) throws URISyntaxException {
        log.debug("REST request to save ForMessages : {}", forMessages);
        if (forMessages.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("forMessages", "idexists", "A new forMessages cannot already have an ID")).body(null);
        }
        ForMessages result = forMessagesRepository.save(forMessages);
        forMessagesSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/for-messages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("forMessages", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /for-messages : Updates an existing forMessages.
     *
     * @param forMessages the forMessages to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated forMessages,
     * or with status 400 (Bad Request) if the forMessages is not valid,
     * or with status 500 (Internal Server Error) if the forMessages couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/for-messages",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ForMessages> updateForMessages(@RequestBody ForMessages forMessages) throws URISyntaxException {
        log.debug("REST request to update ForMessages : {}", forMessages);
        if (forMessages.getId() == null) {
            return createForMessages(forMessages);
        }
        ForMessages result = forMessagesRepository.save(forMessages);
        forMessagesSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("forMessages", forMessages.getId().toString()))
            .body(result);
    }

    /**
     * GET  /for-messages : get all the forMessages.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of forMessages in body
     */
    @RequestMapping(value = "/for-messages",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ForMessages> getAllForMessages() {
        log.debug("REST request to get all ForMessages");
        List<ForMessages> forMessages = forMessagesRepository.findAll();
        return forMessages;
    }

    /**
     * GET  /for-messages/:id : get the "id" forMessages.
     *
     * @param id the id of the forMessages to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the forMessages, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/for-messages/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ForMessages> getForMessages(@PathVariable Long id) {
        log.debug("REST request to get ForMessages : {}", id);
        ForMessages forMessages = forMessagesRepository.findOne(id);
        return Optional.ofNullable(forMessages)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /for-messages/:id : delete the "id" forMessages.
     *
     * @param id the id of the forMessages to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/for-messages/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteForMessages(@PathVariable Long id) {
        log.debug("REST request to delete ForMessages : {}", id);
        forMessagesRepository.delete(id);
        forMessagesSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("forMessages", id.toString())).build();
    }

    /**
     * SEARCH  /_search/for-messages?query=:query : search for the forMessages corresponding
     * to the query.
     *
     * @param query the query of the forMessages search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/for-messages",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ForMessages> searchForMessages(@RequestParam String query) {
        log.debug("REST request to search ForMessages for query {}", query);
        return StreamSupport
            .stream(forMessagesSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}

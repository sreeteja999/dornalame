package com.dornalame.app.web.rest;

import com.dornalame.app.MothersDayApp;
import com.dornalame.app.domain.Reference;
import com.dornalame.app.repository.ReferenceRepository;
import com.dornalame.app.repository.search.ReferenceSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ReferenceResource REST controller.
 *
 * @see ReferenceResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MothersDayApp.class)
@WebAppConfiguration
@IntegrationTest
public class ReferenceResourceIntTest {

    private static final String DEFAULT_REF_NAME = "AAAAA";
    private static final String UPDATED_REF_NAME = "BBBBB";
    private static final String DEFAULT_REF_URL = "AAAAA";
    private static final String UPDATED_REF_URL = "BBBBB";

    @Inject
    private ReferenceRepository referenceRepository;

    @Inject
    private ReferenceSearchRepository referenceSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restReferenceMockMvc;

    private Reference reference;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReferenceResource referenceResource = new ReferenceResource();
        ReflectionTestUtils.setField(referenceResource, "referenceSearchRepository", referenceSearchRepository);
        ReflectionTestUtils.setField(referenceResource, "referenceRepository", referenceRepository);
        this.restReferenceMockMvc = MockMvcBuilders.standaloneSetup(referenceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        referenceSearchRepository.deleteAll();
        reference = new Reference();
        reference.setRef_name(DEFAULT_REF_NAME);
        reference.setRef_url(DEFAULT_REF_URL);
    }

    @Test
    @Transactional
    public void createReference() throws Exception {
        int databaseSizeBeforeCreate = referenceRepository.findAll().size();

        // Create the Reference

        restReferenceMockMvc.perform(post("/public/references")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reference)))
                .andExpect(status().isCreated());

        // Validate the Reference in the database
        List<Reference> references = referenceRepository.findAll();
        assertThat(references).hasSize(databaseSizeBeforeCreate + 1);
        Reference testReference = references.get(references.size() - 1);
        assertThat(testReference.getRef_name()).isEqualTo(DEFAULT_REF_NAME);
        assertThat(testReference.getRef_url()).isEqualTo(DEFAULT_REF_URL);

        // Validate the Reference in ElasticSearch
        Reference referenceEs = referenceSearchRepository.findOne(testReference.getId());
        assertThat(referenceEs).isEqualToComparingFieldByField(testReference);
    }

    @Test
    @Transactional
    public void checkRef_nameIsRequired() throws Exception {
        int databaseSizeBeforeTest = referenceRepository.findAll().size();
        // set the field null
        reference.setRef_name(null);

        // Create the Reference, which fails.

        restReferenceMockMvc.perform(post("/public/references")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reference)))
                .andExpect(status().isBadRequest());

        List<Reference> references = referenceRepository.findAll();
        assertThat(references).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllReferences() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get all the references
        restReferenceMockMvc.perform(get("/public/references?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(reference.getId().intValue())))
                .andExpect(jsonPath("$.[*].ref_name").value(hasItem(DEFAULT_REF_NAME.toString())))
                .andExpect(jsonPath("$.[*].ref_url").value(hasItem(DEFAULT_REF_URL.toString())));
    }

    @Test
    @Transactional
    public void getReference() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);

        // Get the reference
        restReferenceMockMvc.perform(get("/public/references/{id}", reference.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(reference.getId().intValue()))
            .andExpect(jsonPath("$.ref_name").value(DEFAULT_REF_NAME.toString()))
            .andExpect(jsonPath("$.ref_url").value(DEFAULT_REF_URL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingReference() throws Exception {
        // Get the reference
        restReferenceMockMvc.perform(get("/public/references/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReference() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);
        referenceSearchRepository.save(reference);
        int databaseSizeBeforeUpdate = referenceRepository.findAll().size();

        // Update the reference
        Reference updatedReference = new Reference();
        updatedReference.setId(reference.getId());
        updatedReference.setRef_name(UPDATED_REF_NAME);
        updatedReference.setRef_url(UPDATED_REF_URL);

        restReferenceMockMvc.perform(put("/public/references")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedReference)))
                .andExpect(status().isOk());

        // Validate the Reference in the database
        List<Reference> references = referenceRepository.findAll();
        assertThat(references).hasSize(databaseSizeBeforeUpdate);
        Reference testReference = references.get(references.size() - 1);
        assertThat(testReference.getRef_name()).isEqualTo(UPDATED_REF_NAME);
        assertThat(testReference.getRef_url()).isEqualTo(UPDATED_REF_URL);

        // Validate the Reference in ElasticSearch
        Reference referenceEs = referenceSearchRepository.findOne(testReference.getId());
        assertThat(referenceEs).isEqualToComparingFieldByField(testReference);
    }

    @Test
    @Transactional
    public void deleteReference() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);
        referenceSearchRepository.save(reference);
        int databaseSizeBeforeDelete = referenceRepository.findAll().size();

        // Get the reference
        restReferenceMockMvc.perform(delete("/public/references/{id}", reference.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean referenceExistsInEs = referenceSearchRepository.exists(reference.getId());
        assertThat(referenceExistsInEs).isFalse();

        // Validate the database is empty
        List<Reference> references = referenceRepository.findAll();
        assertThat(references).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchReference() throws Exception {
        // Initialize the database
        referenceRepository.saveAndFlush(reference);
        referenceSearchRepository.save(reference);

        // Search the reference
        restReferenceMockMvc.perform(get("/public/_search/references?query=id:" + reference.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reference.getId().intValue())))
            .andExpect(jsonPath("$.[*].ref_name").value(hasItem(DEFAULT_REF_NAME.toString())))
            .andExpect(jsonPath("$.[*].ref_url").value(hasItem(DEFAULT_REF_URL.toString())));
    }
}

package com.dornalame.app.web.rest;

import com.dornalame.app.MothersDayApp;
import com.dornalame.app.domain.ForMessages;
import com.dornalame.app.repository.ForMessagesRepository;
import com.dornalame.app.repository.search.ForMessagesSearchRepository;

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
 * Test class for the ForMessagesResource REST controller.
 *
 * @see ForMessagesResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MothersDayApp.class)
@WebAppConfiguration
@IntegrationTest
public class ForMessagesResourceIntTest {

    private static final String DEFAULT_MESSAGES = "AAAAA";
    private static final String UPDATED_MESSAGES = "BBBBB";

    @Inject
    private ForMessagesRepository forMessagesRepository;

    @Inject
    private ForMessagesSearchRepository forMessagesSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restForMessagesMockMvc;

    private ForMessages forMessages;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ForMessagesResource forMessagesResource = new ForMessagesResource();
        ReflectionTestUtils.setField(forMessagesResource, "forMessagesSearchRepository", forMessagesSearchRepository);
        ReflectionTestUtils.setField(forMessagesResource, "forMessagesRepository", forMessagesRepository);
        this.restForMessagesMockMvc = MockMvcBuilders.standaloneSetup(forMessagesResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        forMessagesSearchRepository.deleteAll();
        forMessages = new ForMessages();
        forMessages.setMessages(DEFAULT_MESSAGES);
    }

    @Test
    @Transactional
    public void createForMessages() throws Exception {
        int databaseSizeBeforeCreate = forMessagesRepository.findAll().size();

        // Create the ForMessages

        restForMessagesMockMvc.perform(post("/public/for-messages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(forMessages)))
                .andExpect(status().isCreated());

        // Validate the ForMessages in the database
        List<ForMessages> forMessages = forMessagesRepository.findAll();
        assertThat(forMessages).hasSize(databaseSizeBeforeCreate + 1);
        ForMessages testForMessages = forMessages.get(forMessages.size() - 1);
        assertThat(testForMessages.getMessages()).isEqualTo(DEFAULT_MESSAGES);

        // Validate the ForMessages in ElasticSearch
        ForMessages forMessagesEs = forMessagesSearchRepository.findOne(testForMessages.getId());
        assertThat(forMessagesEs).isEqualToComparingFieldByField(testForMessages);
    }

    @Test
    @Transactional
    public void getAllForMessages() throws Exception {
        // Initialize the database
        forMessagesRepository.saveAndFlush(forMessages);

        // Get all the forMessages
        restForMessagesMockMvc.perform(get("/public/for-messages?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(forMessages.getId().intValue())))
                .andExpect(jsonPath("$.[*].messages").value(hasItem(DEFAULT_MESSAGES.toString())));
    }

    @Test
    @Transactional
    public void getForMessages() throws Exception {
        // Initialize the database
        forMessagesRepository.saveAndFlush(forMessages);

        // Get the forMessages
        restForMessagesMockMvc.perform(get("/public/for-messages/{id}", forMessages.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(forMessages.getId().intValue()))
            .andExpect(jsonPath("$.messages").value(DEFAULT_MESSAGES.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingForMessages() throws Exception {
        // Get the forMessages
        restForMessagesMockMvc.perform(get("/public/for-messages/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateForMessages() throws Exception {
        // Initialize the database
        forMessagesRepository.saveAndFlush(forMessages);
        forMessagesSearchRepository.save(forMessages);
        int databaseSizeBeforeUpdate = forMessagesRepository.findAll().size();

        // Update the forMessages
        ForMessages updatedForMessages = new ForMessages();
        updatedForMessages.setId(forMessages.getId());
        updatedForMessages.setMessages(UPDATED_MESSAGES);

        restForMessagesMockMvc.perform(put("/public/for-messages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedForMessages)))
                .andExpect(status().isOk());

        // Validate the ForMessages in the database
        List<ForMessages> forMessages = forMessagesRepository.findAll();
        assertThat(forMessages).hasSize(databaseSizeBeforeUpdate);
        ForMessages testForMessages = forMessages.get(forMessages.size() - 1);
        assertThat(testForMessages.getMessages()).isEqualTo(UPDATED_MESSAGES);

        // Validate the ForMessages in ElasticSearch
        ForMessages forMessagesEs = forMessagesSearchRepository.findOne(testForMessages.getId());
        assertThat(forMessagesEs).isEqualToComparingFieldByField(testForMessages);
    }

    @Test
    @Transactional
    public void deleteForMessages() throws Exception {
        // Initialize the database
        forMessagesRepository.saveAndFlush(forMessages);
        forMessagesSearchRepository.save(forMessages);
        int databaseSizeBeforeDelete = forMessagesRepository.findAll().size();

        // Get the forMessages
        restForMessagesMockMvc.perform(delete("/public/for-messages/{id}", forMessages.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean forMessagesExistsInEs = forMessagesSearchRepository.exists(forMessages.getId());
        assertThat(forMessagesExistsInEs).isFalse();

        // Validate the database is empty
        List<ForMessages> forMessages = forMessagesRepository.findAll();
        assertThat(forMessages).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchForMessages() throws Exception {
        // Initialize the database
        forMessagesRepository.saveAndFlush(forMessages);
        forMessagesSearchRepository.save(forMessages);

        // Search the forMessages
        restForMessagesMockMvc.perform(get("/public/_search/for-messages?query=id:" + forMessages.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(forMessages.getId().intValue())))
            .andExpect(jsonPath("$.[*].messages").value(hasItem(DEFAULT_MESSAGES.toString())));
    }
}

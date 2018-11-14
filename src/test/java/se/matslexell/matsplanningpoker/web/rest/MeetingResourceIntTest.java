package se.matslexell.matsplanningpoker.web.rest;

import org.springframework.security.core.userdetails.UserDetailsService;
import se.matslexell.matsplanningpoker.MatsPlanningPokerApp;

import se.matslexell.matsplanningpoker.domain.Meeting;
import se.matslexell.matsplanningpoker.repository.MeetingRepository;
import se.matslexell.matsplanningpoker.security.jwt.TokenProvider;
import se.matslexell.matsplanningpoker.service.MeetingService;
import se.matslexell.matsplanningpoker.service.ParticipantService;
import se.matslexell.matsplanningpoker.service.dto.MeetingDTO;
import se.matslexell.matsplanningpoker.service.mapper.MeetingMapper;
import se.matslexell.matsplanningpoker.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static se.matslexell.matsplanningpoker.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MeetingResource REST controller.
 *
 * @see MeetingResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MatsPlanningPokerApp.class)
public class MeetingResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UUID = "AAAAAAAAAA";
    private static final String UPDATED_UUID = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MeetingMapper meetingMapper;
    
    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;
    
    @Autowired
    private ParticipantService participantService;
    
    @Autowired
    private TokenProvider tokenProvider;
    
    @Autowired
    private UserDetailsService userDetailsService;

    private MockMvc restMeetingMockMvc;

    private Meeting meeting;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MeetingResource meetingResource = new MeetingResource(meetingService, participantService, tokenProvider, userDetailsService);
        this.restMeetingMockMvc = MockMvcBuilders.standaloneSetup(meetingResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Meeting createEntity(EntityManager em) {
        Meeting meeting = new Meeting()
            .name(DEFAULT_NAME)
            .uuid(DEFAULT_UUID)
            .createdDate(DEFAULT_CREATED_DATE);
        return meeting;
    }

    @Before
    public void initTest() {
        meeting = createEntity(em);
    }

    @Test
    @Transactional
    public void createMeeting() throws Exception {
        int databaseSizeBeforeCreate = meetingRepository.findAll().size();

        // Create the Meeting
        MeetingDTO meetingDTO = meetingMapper.toDto(meeting);
        restMeetingMockMvc.perform(post("/api/meetings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(meetingDTO)))
            .andExpect(status().isCreated());

        // Validate the Meeting in the database
        List<Meeting> meetingList = meetingRepository.findAll();
        assertThat(meetingList).hasSize(databaseSizeBeforeCreate + 1);
        Meeting testMeeting = meetingList.get(meetingList.size() - 1);
        assertThat(testMeeting.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMeeting.getUuid()).isEqualTo(DEFAULT_UUID);
        assertThat(testMeeting.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
    }

    @Test
    @Transactional
    public void createMeetingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = meetingRepository.findAll().size();

        // Create the Meeting with an existing ID
        meeting.setId(1L);
        MeetingDTO meetingDTO = meetingMapper.toDto(meeting);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMeetingMockMvc.perform(post("/api/meetings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(meetingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Meeting in the database
        List<Meeting> meetingList = meetingRepository.findAll();
        assertThat(meetingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMeetings() throws Exception {
        // Initialize the database
        meetingRepository.saveAndFlush(meeting);

        // Get all the meetingList
        restMeetingMockMvc.perform(get("/api/meetings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(meeting.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].uuid").value(hasItem(DEFAULT_UUID.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getMeeting() throws Exception {
        // Initialize the database
        meetingRepository.saveAndFlush(meeting);

        // Get the meeting
        restMeetingMockMvc.perform(get("/api/meetings/{id}", meeting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(meeting.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.uuid").value(DEFAULT_UUID.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMeeting() throws Exception {
        // Get the meeting
        restMeetingMockMvc.perform(get("/api/meetings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMeeting() throws Exception {
        // Initialize the database
        meetingRepository.saveAndFlush(meeting);

        int databaseSizeBeforeUpdate = meetingRepository.findAll().size();

        // Update the meeting
        Meeting updatedMeeting = meetingRepository.findById(meeting.getId()).get();
        // Disconnect from session so that the updates on updatedMeeting are not directly saved in db
        em.detach(updatedMeeting);
        updatedMeeting
            .name(UPDATED_NAME)
            .uuid(UPDATED_UUID)
            .createdDate(UPDATED_CREATED_DATE);
        MeetingDTO meetingDTO = meetingMapper.toDto(updatedMeeting);

        restMeetingMockMvc.perform(put("/api/meetings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(meetingDTO)))
            .andExpect(status().isOk());

        // Validate the Meeting in the database
        List<Meeting> meetingList = meetingRepository.findAll();
        assertThat(meetingList).hasSize(databaseSizeBeforeUpdate);
        Meeting testMeeting = meetingList.get(meetingList.size() - 1);
        assertThat(testMeeting.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMeeting.getUuid()).isEqualTo(UPDATED_UUID);
        assertThat(testMeeting.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingMeeting() throws Exception {
        int databaseSizeBeforeUpdate = meetingRepository.findAll().size();

        // Create the Meeting
        MeetingDTO meetingDTO = meetingMapper.toDto(meeting);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMeetingMockMvc.perform(put("/api/meetings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(meetingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Meeting in the database
        List<Meeting> meetingList = meetingRepository.findAll();
        assertThat(meetingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteMeeting() throws Exception {
        // Initialize the database
        meetingRepository.saveAndFlush(meeting);

        int databaseSizeBeforeDelete = meetingRepository.findAll().size();

        // Get the meeting
        restMeetingMockMvc.perform(delete("/api/meetings/{id}", meeting.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Meeting> meetingList = meetingRepository.findAll();
        assertThat(meetingList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Meeting.class);
        Meeting meeting1 = new Meeting();
        meeting1.setId(1L);
        Meeting meeting2 = new Meeting();
        meeting2.setId(meeting1.getId());
        assertThat(meeting1).isEqualTo(meeting2);
        meeting2.setId(2L);
        assertThat(meeting1).isNotEqualTo(meeting2);
        meeting1.setId(null);
        assertThat(meeting1).isNotEqualTo(meeting2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MeetingDTO.class);
        MeetingDTO meetingDTO1 = new MeetingDTO();
        meetingDTO1.setId(1L);
        MeetingDTO meetingDTO2 = new MeetingDTO();
        assertThat(meetingDTO1).isNotEqualTo(meetingDTO2);
        meetingDTO2.setId(meetingDTO1.getId());
        assertThat(meetingDTO1).isEqualTo(meetingDTO2);
        meetingDTO2.setId(2L);
        assertThat(meetingDTO1).isNotEqualTo(meetingDTO2);
        meetingDTO1.setId(null);
        assertThat(meetingDTO1).isNotEqualTo(meetingDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(meetingMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(meetingMapper.fromId(null)).isNull();
    }
    
    @Test
    @Transactional
    public void testFindCreatedDateFromMeetingId() {
        Instant time = Instant.ofEpochMilli(24601);
        Meeting meeting = MeetingResourceIntTest.createEntity(em).createdDate(time);
        meeting = meetingRepository.save(meeting);
    
        assertEquals(meetingRepository.findCreatedDateFromMeetingId(meeting.getId()).get(), time);
        assertEquals(meetingRepository.findCreatedDateFromMeetingId(5467346l).isPresent(), false);
    }
}

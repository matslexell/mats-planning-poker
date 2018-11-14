package se.matslexell.matsplanningpoker.service;

import se.matslexell.matsplanningpoker.domain.Meeting;
import se.matslexell.matsplanningpoker.domain.Participant;
import se.matslexell.matsplanningpoker.repository.MeetingRepository;
import se.matslexell.matsplanningpoker.service.dto.MeetingDTO;
import se.matslexell.matsplanningpoker.service.mapper.MeetingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Service Implementation for managing Meeting.
 */
@Service
@Transactional
public class MeetingService {

    private final Logger log = LoggerFactory.getLogger(MeetingService.class);

    private final MeetingRepository meetingRepository;

    private final MeetingMapper meetingMapper;

    public MeetingService(MeetingRepository meetingRepository, MeetingMapper meetingMapper) {
        this.meetingRepository = meetingRepository;
        this.meetingMapper = meetingMapper;
    }

    /**
     * Save a meeting.
     *
     * @param meetingDTO the entity to save
     * @return the persisted entity
     */
    public MeetingDTO save(MeetingDTO meetingDTO) {
        log.debug("Request to save Meeting : {}", meetingDTO);
        Meeting meeting = meetingMapper.toEntity(meetingDTO);
        meeting.createdDate(meetingRepository.findCreatedDateFromMeetingId(meeting.getId()).orElse(Instant.now()));
        meeting = meetingRepository.save(meeting);
        return meetingMapper.toDto(meeting);
    }

    /**
     * Get all the meetings.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MeetingDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Meetings");
        return meetingRepository.findAll(pageable)
            .map(meetingMapper::toDto);
    }


    /**
     * Get one meeting by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<MeetingDTO> findOne(Long id) {
        log.debug("Request to get Meeting : {}", id);
        return meetingRepository.findById(id)
            .map(meetingMapper::toDto);
    }

    /**
     * Delete the meeting by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Meeting : {}", id);
        meetingRepository.deleteById(id);
    }
	
	public void addParticipantToMeeting(Participant participant, String meetingUuid) { // TODO
		log.debug("Request to add Participant : {}, to Meeting with uuid : {}", participant, meetingUuid);
	
		Optional<Meeting> meetingOptional = meetingRepository.findByUuid(meetingUuid);
        if(!meetingOptional.isPresent()) {
            throw new RuntimeException("No meeting found with uuid: " + meetingUuid);
        }
        
        meetingOptional.get().addParticipant(participant);
        meetingRepository.save(meetingOptional.get());
	}
    
    public boolean existsByMeetingUuid(String meetingUuid) {
        return meetingRepository.findByUuid(meetingUuid).isPresent();
    }
    
    public Optional<MeetingDTO> findByUuid(String uudi) {
        return meetingRepository.findByUuid(uudi)
                .map(meetingMapper::toDto);
    }
}

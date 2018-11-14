package se.matslexell.matsplanningpoker.service;

import se.matslexell.matsplanningpoker.domain.Participant;
import se.matslexell.matsplanningpoker.repository.ParticipantRepository;
import se.matslexell.matsplanningpoker.service.dto.ParticipantDTO;
import se.matslexell.matsplanningpoker.service.mapper.ParticipantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Service Implementation for managing Participant.
 */
@Service
@Transactional
public class ParticipantService {

    private final Logger log = LoggerFactory.getLogger(ParticipantService.class);

    private final ParticipantRepository participantRepository;

    private final ParticipantMapper participantMapper;

    public ParticipantService(ParticipantRepository participantRepository, ParticipantMapper participantMapper) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
    }

    /**
     * Save a participant.
     *
     * @param participantDTO the entity to save
     * @return the persisted entity
     */
    public ParticipantDTO save(ParticipantDTO participantDTO) {
        log.debug("Request to save Participant : {}", participantDTO);
        Participant participant = participantMapper.toEntity(participantDTO);
        
        participant.setToken(participantRepository.findTokenFromParticipantId(participant.getId()).orElse(generateToken()));
        
        participant = participantRepository.save(participant);
        return participantMapper.toDto(participant);
    }

    /**
     * Get all the participants.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ParticipantDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Participants");
        return participantRepository.findAll(pageable)
            .map(participantMapper::toDto);
    }


    /**
     * Get one participant by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<ParticipantDTO> findOne(Long id) {
        log.debug("Request to get Participant : {}", id);
        return participantRepository.findById(id)
            .map(participantMapper::toDto);
    }

    /**
     * Delete the participant by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Participant : {}", id);
        participantRepository.deleteById(id);
    }
	
	public boolean existsByMeetingUuidAndParticipantToken(String meetingUuid, String token) {
        return false;
	}
    
    public void deleteByToken(String token) {
        participantRepository.deleteByToken(token);
    }
    
    public Participant createAndSaveNewParticipant(String name) {
        log.debug("Request to create and save new participant with name : {}", name);
        Participant participant = new Participant().token(generateToken()).name(name);
        return participantRepository.save(participant);
    }
	
	public Optional<ParticipantDTO> findByToken(String token) {
        log.debug("Request to get Participant by token : {}", token);
        return participantRepository.findByToken(token)
                .map(participantMapper::toDto);
	}
	
	public boolean existsByToken(String token) {
        return participantRepository.findByToken(token).isPresent();
	}
    
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}

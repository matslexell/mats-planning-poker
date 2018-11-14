package se.matslexell.matsplanningpoker.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import se.matslexell.matsplanningpoker.domain.Participant;
import se.matslexell.matsplanningpoker.security.SecurityUtils;
import se.matslexell.matsplanningpoker.security.jwt.JWTFilter;
import se.matslexell.matsplanningpoker.security.jwt.TokenProvider;
import se.matslexell.matsplanningpoker.service.MeetingService;
import se.matslexell.matsplanningpoker.service.ParticipantService;
import se.matslexell.matsplanningpoker.web.rest.errors.BadRequestAlertException;
import se.matslexell.matsplanningpoker.web.rest.util.HeaderUtil;
import se.matslexell.matsplanningpoker.web.rest.util.PaginationUtil;
import se.matslexell.matsplanningpoker.service.dto.MeetingDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.matslexell.matsplanningpoker.web.rest.vm.LoginVM;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing Meeting.
 */
@RestController
@RequestMapping("/api")
public class MeetingResource {
	
	private final Logger log = LoggerFactory.getLogger(MeetingResource.class);
	
	private static final String ENTITY_NAME = "meeting";
	
	private final MeetingService meetingService;
	
	private final ParticipantService participantService;
	
	private final TokenProvider tokenProvider;
	
	private UserDetailsService userDetailsService;
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	public MeetingResource(MeetingService meetingService, ParticipantService participantService, TokenProvider tokenProvider, UserDetailsService userDetailsService) {
		this.participantService = participantService;
		this.meetingService = meetingService;
		this.tokenProvider = tokenProvider;
		this.userDetailsService = userDetailsService;
	}
	
	/**
	 * POST  /meetings : Create a new meeting.
	 *
	 * @param meetingDTO the meetingDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new meetingDTO, or with status 400 (Bad
	 * Request) if the meeting has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PostMapping("/meetings")
	@Timed
	public ResponseEntity<MeetingDTO> createMeeting(@RequestBody MeetingDTO meetingDTO) throws URISyntaxException {
		log.debug("REST request to save Meeting : {}", meetingDTO);
		if (meetingDTO.getId() != null) {
			throw new BadRequestAlertException("A new meeting cannot already have an ID", ENTITY_NAME, "idexists");
		}
		
		MeetingDTO result = meetingService.save(meetingDTO);
		return ResponseEntity.created(new URI("/api/meetings/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
				.body(result);
	}
	
	/**
	 * POST  /meetings : Create a new meeting.
	 *
	 * @param meetingDTO the meetingDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new meetingDTO, or with status 400 (Bad
	 * Request) if the meeting has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PostMapping("/meetings/fromName")
	@Timed
	public ResponseEntity<MeetingDTO> createMeetingFromName(@RequestParam(value = "name") String name) throws URISyntaxException {
		log.debug("REST request to save from name Meeting : {}", name);
		MeetingDTO meetingDTO = new MeetingDTO();
		meetingDTO.setName(name);
		meetingDTO.setUuid(randomUuid());
		return createMeeting(meetingDTO);
	}
	
	/**
	 * PUT  /meetings : Updates an existing meeting.
	 *
	 * @param meetingDTO the meetingDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated meetingDTO, or with status 400 (Bad
	 * Request) if the meetingDTO is not valid, or with status 500 (Internal Server Error) if the meetingDTO couldn't be
	 * updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PutMapping("/meetings")
	@Timed
	public ResponseEntity<MeetingDTO> updateMeeting(@RequestBody MeetingDTO meetingDTO) throws URISyntaxException {
		log.debug("REST request to update Meeting : {}", meetingDTO);
		if (meetingDTO.getId() == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		MeetingDTO result = meetingService.save(meetingDTO);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, meetingDTO.getId().toString()))
				.body(result);
	}
	
	/**
	 * PUT  /meetings : Updates an existing meeting.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the updated meetingDTO, or with status 400 (Bad
	 * Request) if the meetingDTO is not valid, or with status 500 (Internal Server Error) if the meetingDTO couldn't be
	 * updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PutMapping("/meetings/join/{meetingUuid}/{participantName}")
	@Timed
	public ResponseEntity<String> joinMeeting(@PathVariable String meetingUuid, @PathVariable String participantName) throws URISyntaxException, JsonProcessingException {
		log.debug("REST request to join meeting : {}, participantName : {}", meetingUuid, participantName);
		
		if (!meetingService.existsByMeetingUuid(meetingUuid)) {
			log.error("No meeting exists by meetingUuid : " + meetingUuid);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Participant participant = participantService.createAndSaveNewParticipant(participantName);
		meetingService.addParticipantToMeeting(participant, meetingUuid);
		
		return new ResponseEntity<>(mapper.writeValueAsString(participant.getJwt()), HttpStatus.OK);
	}
	
	/**
	 * GET  /meetings : get all the meetings.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of meetings in body
	 */
	@GetMapping("/meetings")
	@Timed
	public ResponseEntity<List<MeetingDTO>> getAllMeetings(Pageable pageable) {
		log.debug("REST request to get a page of Meetings");
		Page<MeetingDTO> page = meetingService.findAll(pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/meetings");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}
	
	/**
	 * GET  /meetings/:id : get the "id" meeting.
	 *
	 * @param id the id of the meetingDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the meetingDTO, or with status 404 (Not Found)
	 */
	@GetMapping("/meetings/{id}")
	@Timed
	public ResponseEntity<MeetingDTO> getMeeting(@PathVariable Long id) {
		log.debug("REST request to get Meeting : {}", id);
		Optional<MeetingDTO> meetingDTO = meetingService.findOne(id);
		return ResponseUtil.wrapOrNotFound(meetingDTO);
	}
	
	/**
	 * GET  /meetings/:id : get the "id" meeting.
	 *
	 * @return the ResponseEntity with status 200 (OK) and with body the meetingDTO, or with status 404 (Not Found)
	 */
	@GetMapping("/meetings/uuid/{uuid}")
	@Timed
	public ResponseEntity<MeetingDTO> getMeetingFromUuid(@PathVariable String uuid) {
		Optional<MeetingDTO> meetingDTO = meetingService.findByUuid(uuid);
		return ResponseUtil.wrapOrNotFound(meetingDTO);
	}
	
	/**
	 * DELETE  /meetings/:id : delete the "id" meeting.
	 *
	 * @param id the id of the meetingDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/meetings/{id}")
	@Timed
	public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
		log.debug("REST request to delete Meeting : {}", id);
		meetingService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
	}
	
	private ResponseEntity<UserJWTController.JWTToken> responseEntityFromJwt(String jwt) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
		return new ResponseEntity<>(new UserJWTController.JWTToken(jwt), httpHeaders, HttpStatus.OK);
		
	}
	
	private boolean jwtIsPresentAndNonEmpty() {
		return SecurityUtils.getCurrentUserJWT().isPresent() &&
				!SecurityUtils.getCurrentUserJWT().get().replace(" ", "").isEmpty();
	}
	
	private String randomUuid() {
		return UUID.randomUUID().toString().substring(24, 36);
		
		// Create a pretty link like at goo.gl or bitly:
//		IntStream intStream = concat(range(65, 90+1), concat(range(97, 122+1), range(48, 57+1)));
//		int[] array = intStream.toArray();
//
//		StringBuilder stringBuilder = new StringBuilder();
//
//		for (int i = 0; i < 8; i++) {
//			stringBuilder.append((char) array[(int) (Math.random() * array.length)]);
//		}
//		return stringBuilder.toString();
	}
}

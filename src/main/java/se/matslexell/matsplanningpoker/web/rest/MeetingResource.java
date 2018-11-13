package se.matslexell.matsplanningpoker.web.rest;

import com.codahale.metrics.annotation.Timed;
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
	 * @param meetingDTO the meetingDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated meetingDTO, or with status 400 (Bad
	 * Request) if the meetingDTO is not valid, or with status 500 (Internal Server Error) if the meetingDTO couldn't be
	 * updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PutMapping("/meetings/join/{meetingUuid}/{participantName}")
	@Timed
	public ResponseEntity<UserJWTController.JWTToken> joinMeeting(@PathVariable String meetingUuid, @PathVariable String participantName) throws URISyntaxException {
		log.debug("REST request to join meeting : {}, participantName : {}", meetingUuid, participantName);
		
		if (!meetingService.existsByMeetingUuid(meetingUuid)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		log.debug("USERLOGIN is: " + SecurityUtils.getCurrentUserLogin());
		
		if (jwtIsPresentAndNonEmpty()) {
			log.debug("JWT is: " + SecurityUtils.getCurrentUserJWT().get());
			
			// Participant is already a member of the group. He/she is probably only trying to return to the site.
			if (participantService.existsByMeetingUuidAndParticipantJwt(meetingUuid, SecurityUtils.getCurrentUserJWT().get())) {
				return responseEntityFromJwt(SecurityUtils.getCurrentUserJWT().get());
			} else {
				// Else delete the participant from any group and enter this one
				participantService.deleteByJwt(SecurityUtils.getCurrentUserJWT().get());
			}
		}
		
		String jwt = generateJwt();
		
		Participant participant = participantService.createAndSaveNewParticipant(participantName, jwt);
		meetingService.addParticipantToMeeting(participant, meetingUuid);
		return responseEntityFromJwt(jwt);
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
	 * @param id the id of the meetingDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the meetingDTO, or with status 404 (Not Found)
	 */
	@GetMapping("/meetings/uuid/{uuid}")
	@Timed
	public ResponseEntity<MeetingDTO> getMeetingFromUuid(@PathVariable String uuid) {
		log.debug("REST request to get Meeting with uuid : {}", uuid);
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
	
	private String generateJwt() {
		String principal = "user";
		
		UserDetails studentDetails = userDetailsService.loadUserByUsername(principal);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, studentDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = tokenProvider.createToken(authentication, false);
		
		return jwt;
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
}

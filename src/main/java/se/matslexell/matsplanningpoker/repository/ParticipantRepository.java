package se.matslexell.matsplanningpoker.repository;

import org.springframework.data.repository.query.Param;
import se.matslexell.matsplanningpoker.domain.Participant;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import se.matslexell.matsplanningpoker.service.dto.ParticipantDTO;

import java.util.Optional;


/**
 * Spring Data  repository for the Participant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
	
	void deleteByJwt(String jwt);
	
	Optional<Participant> findByJwt(String jwt);
	
	@Query("select participant.jwt from Participant participant where participant.id = :id")
	Optional<String> findJwtFromParticipantId(@Param("id") Long id);
	
}

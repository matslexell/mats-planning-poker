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
	
	void deleteByToken(String token);
	
	Optional<Participant> findByToken(String token);
	
	@Query("select participant.token from Participant participant where participant.id = :id")
	Optional<String> findTokenFromParticipantId(@Param("id") Long id);
	
}

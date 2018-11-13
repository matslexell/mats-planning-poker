package se.matslexell.matsplanningpoker.repository;

import se.matslexell.matsplanningpoker.domain.Meeting;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Meeting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
	
	Optional<Meeting> findByUuid(String uuid);
}

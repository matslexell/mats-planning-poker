package se.matslexell.matsplanningpoker.repository;

import org.springframework.data.repository.query.Param;
import se.matslexell.matsplanningpoker.domain.Meeting;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;


/**
 * Spring Data  repository for the Meeting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
	
	Optional<Meeting> findByUuid(String uuid);
	
	@Query("select meeting.createdDate from Meeting meeting where meeting.id = :id")
	Optional<Instant> findCreatedDateFromMeetingId(@Param("id") Long id);
}

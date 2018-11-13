package se.matslexell.matsplanningpoker.service.mapper;

import se.matslexell.matsplanningpoker.domain.*;
import se.matslexell.matsplanningpoker.service.dto.MeetingDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Meeting and its DTO MeetingDTO.
 */
@Mapper(componentModel = "spring", uses = {ParticipantMapper.class})
public interface MeetingMapper extends EntityMapper<MeetingDTO, Meeting> {
	
    @Mapping(target = "createdDate", ignore = true)
    Meeting toEntity(MeetingDTO meetingDTO);

    default Meeting fromId(Long id) {
        if (id == null) {
            return null;
        }
        Meeting meeting = new Meeting();
        meeting.setId(id);
        return meeting;
    }
}

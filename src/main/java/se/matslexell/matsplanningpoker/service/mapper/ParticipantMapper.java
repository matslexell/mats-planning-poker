package se.matslexell.matsplanningpoker.service.mapper;

import se.matslexell.matsplanningpoker.domain.*;
import se.matslexell.matsplanningpoker.service.dto.ParticipantDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Participant and its DTO ParticipantDTO.
 */
@Mapper(componentModel = "spring", uses = {MeetingMapper.class})
public interface ParticipantMapper extends EntityMapper<ParticipantDTO, Participant> {

    @Mapping(source = "meeting.id", target = "meetingId")
    @Mapping(source = "meeting.name", target = "meetingName")
    ParticipantDTO toDto(Participant participant);

    @Mapping(source = "meetingId", target = "meeting")
    @Mapping(target = "jwt", ignore = true)
    Participant toEntity(ParticipantDTO participantDTO);

    default Participant fromId(Long id) {
        if (id == null) {
            return null;
        }
        Participant participant = new Participant();
        participant.setId(id);
        return participant;
    }
}

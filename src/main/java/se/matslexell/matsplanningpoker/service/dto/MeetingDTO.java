package se.matslexell.matsplanningpoker.service.dto;

import java.time.Instant;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the Meeting entity.
 */
public class MeetingDTO implements Serializable {

    private Long id;

    private String name;

    private String uuid;
    
    private Set<ParticipantDTO> participants;
    
    public Set<ParticipantDTO> getParticipants() {
        return participants;
    }
    
    public void setParticipants(Set<ParticipantDTO> participants) {
        this.participants = participants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MeetingDTO meetingDTO = (MeetingDTO) o;
        if (meetingDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), meetingDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
    
    @Override
    public String toString() {
        return "MeetingDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", participants=" + participants +
                '}';
    }
}

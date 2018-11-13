package se.matslexell.matsplanningpoker.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Participant entity.
 */
public class ParticipantDTO implements Serializable {

    private Long id;

    private String name;

    private String vote;
    
    private Long meetingId;

    private String meetingName;

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

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParticipantDTO participantDTO = (ParticipantDTO) o;
        if (participantDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), participantDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ParticipantDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", vote='" + getVote() + "'" +
            ", meeting=" + getMeetingId() +
            ", meeting='" + getMeetingName() + "'" +
            "}";
    }
}

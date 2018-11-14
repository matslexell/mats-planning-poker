package se.matslexell.matsplanningpoker.web.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import se.matslexell.matsplanningpoker.service.ParticipantService;
import se.matslexell.matsplanningpoker.web.websocket.dto.ActivityDTO;

import java.security.Principal;
import java.time.Instant;
import java.util.HashMap;

import static se.matslexell.matsplanningpoker.config.WebsocketConfiguration.IP_ADDRESS;

@Controller
public class MeetingUpdateService implements ApplicationListener<SessionDisconnectEvent> {

    private static final Logger log = LoggerFactory.getLogger(MeetingUpdateService.class);

    private final SimpMessageSendingOperations messagingTemplate;
    
    private final ParticipantService participantService;
    
    HashMap<String, String> sessionIdToMeetingUuidMap = new HashMap<>();
    HashMap<String, String> sessionIdToParticipantJwtMap = new HashMap<>();

    public MeetingUpdateService(SimpMessageSendingOperations messagingTemplate, ParticipantService participantService) {
        this.messagingTemplate = messagingTemplate;
        this.participantService = participantService;
    }

    @MessageMapping("/meetingUpdate/server/{meetingUuid}/{jwt}")
    public void sendActivity(@DestinationVariable String meetingUuid, @DestinationVariable String jwt,
                             StompHeaderAccessor stompHeaderAccessor) throws InterruptedException {
        log.debug("Received activity with meetingId : {}, jwt : {}", meetingUuid, jwt);
        sessionIdToMeetingUuidMap.put(stompHeaderAccessor.getSessionId(), meetingUuid);
        sessionIdToParticipantJwtMap.put(stompHeaderAccessor.getSessionId(), jwt);
        messagingTemplate.convertAndSend("/meetingUpdate/client/" + meetingUuid, new ActivityDTO());
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        log.debug("On Application event {}, meetingId : {}", event, sessionIdToMeetingUuidMap.get(event.getSessionId()));
        sessionIdToParticipantJwtMap.computeIfPresent(event.getSessionId(), (id, jwt) -> {
            log.debug("In computeIfPresent sessionIdToParticipantJwtMap, participantId : {}", jwt);
            log.debug("Part: {}", participantService.findByJwt(jwt));
    
            participantService.deleteByJwt(jwt);
            log.debug("Part after delete: {}", participantService.findByJwt(jwt));
    
            log.debug("In computeIfPresent sessionIdToParticipantJwtMap, participantId : {}", jwt);
    
            return null;
        });
        sessionIdToMeetingUuidMap.computeIfPresent(event.getSessionId(), (id, meetingUuid) -> {
            log.debug("In computeIfPresent sessionIdToMeetingUuidMap, meetingId : {}", meetingUuid);
            messagingTemplate.convertAndSend("/meetingUpdate/client/" + meetingUuid, new ActivityDTO());
            return null;
        });
    }
}

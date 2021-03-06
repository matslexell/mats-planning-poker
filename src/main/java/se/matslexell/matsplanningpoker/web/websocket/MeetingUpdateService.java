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
    HashMap<String, String> sessionIdToParticipantTokenMap = new HashMap<>();

    public MeetingUpdateService(SimpMessageSendingOperations messagingTemplate, ParticipantService participantService) {
        this.messagingTemplate = messagingTemplate;
        this.participantService = participantService;
    }

    /**
     * Informs that somone has voted or joined a meeting.
     *
     * @param meetingUuid
     * @param token
     * @param stompHeaderAccessor
     * @throws InterruptedException
     */
    @MessageMapping("/meetingUpdate/server/{meetingUuid}/{token}")
    public void sendActivity(@DestinationVariable String meetingUuid, @DestinationVariable String token,
                             StompHeaderAccessor stompHeaderAccessor) throws InterruptedException {
        log.debug("Received activity with meetingId : {}, token : {}", meetingUuid, token);
        sessionIdToMeetingUuidMap.put(stompHeaderAccessor.getSessionId(), meetingUuid);
        sessionIdToParticipantTokenMap.put(stompHeaderAccessor.getSessionId(), token);
        messagingTemplate.convertAndSend("/meetingUpdate/client/" + meetingUuid, new ActivityDTO());
    }

    /**
     * When someone closes a connection.
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        log.debug("On Application event {}, meetingId : {}", event, sessionIdToMeetingUuidMap.get(event.getSessionId()));
        sessionIdToParticipantTokenMap.computeIfPresent(event.getSessionId(), (id, token) -> {
            participantService.deleteByToken(token);
            return null;
        });
        sessionIdToMeetingUuidMap.computeIfPresent(event.getSessionId(), (id, meetingUuid) -> {
            messagingTemplate.convertAndSend("/meetingUpdate/client/" + meetingUuid, new ActivityDTO());
            return null;
        });
    }
}

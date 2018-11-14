package se.matslexell.matsplanningpoker.web.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import se.matslexell.matsplanningpoker.web.websocket.dto.ActivityDTO;

import java.security.Principal;
import java.time.Instant;

import static se.matslexell.matsplanningpoker.config.WebsocketConfiguration.IP_ADDRESS;

@Controller
public class MeetingUpdateService implements ApplicationListener<SessionDisconnectEvent> {

    private static final Logger log = LoggerFactory.getLogger(MeetingUpdateService.class);

    private final SimpMessageSendingOperations messagingTemplate;

    public MeetingUpdateService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/meetingUpdate/server/{meetingUuid}")
    public void sendActivity(@DestinationVariable String meetingUuid, @Payload ActivityDTO activityDTO, StompHeaderAccessor stompHeaderAccessor, Principal principal) throws InterruptedException {
        activityDTO.setUserLogin(principal.getName());
        activityDTO.setSessionId(stompHeaderAccessor.getSessionId());
        activityDTO.setIpAddress(stompHeaderAccessor.getSessionAttributes().get(IP_ADDRESS).toString());
        activityDTO.setTime(Instant.now());
        log.debug("Sending user tracking data {}", activityDTO);
        Thread.sleep(1000);
        log.debug("Sending /meetingUpdate/client/" + meetingUuid);
        messagingTemplate.convertAndSend("/meetingUpdate/client/" + meetingUuid, activityDTO);
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setSessionId(event.getSessionId());
        activityDTO.setPage("logout");
        log.debug("On Application event {}", event);
        messagingTemplate.convertAndSend("/meetingUpdate/client", activityDTO);
    }
}

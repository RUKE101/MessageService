package ru.afonskiy.messenger.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import ru.afonskiy.messenger.entity.MessageEntity;
import ru.afonskiy.messenger.service.MessageService;

import java.util.List;

@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getUser().getName();

        List<MessageEntity> unreadMessages = messageService.getMessages(username,headerAccessor.getSessionId());


        for (MessageEntity message : unreadMessages) {
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/messages",
                    message
            );
        }
    }
}


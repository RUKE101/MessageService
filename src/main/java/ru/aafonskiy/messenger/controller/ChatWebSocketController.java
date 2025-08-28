package ru.aafonskiy.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.aafonskiy.messenger.entity.MessageEntity;
import ru.aafonskiy.messenger.service.MessageService;
import ru.aafonskiy.messenger.util.JwtUtils;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtUtils jwtUtils;

    @MessageMapping("/chat")
    public void handleMessage(MessageEntity message, SimpMessageHeaderAccessor headers) {
        String token = (String) headers.getSessionAttributes().get("jwt"); // Получаем jwt

        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("JWT токен не валиден");
        }

        String senderUsername = jwtUtils.getUsernameFromToken(token); // Получаем имя отправителя
        message.setSender(senderUsername);
        message.setTimestamp(Instant.now().toString());

        messageService.createdMessage(message);

        // отправка конкретному получателю
        simpMessagingTemplate.convertAndSendToUser(
                message.getRecipient(),
                "/queue/messages",
                message
        );
    }
}

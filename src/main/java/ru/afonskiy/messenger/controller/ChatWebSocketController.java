package ru.afonskiy.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.afonskiy.messenger.entity.MessageEntity;
import ru.afonskiy.messenger.service.MessageService;
import ru.afonskiy.messenger.jwt.util.JwtUtils;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtUtils jwtUtils;

    @MessageMapping("/chat")
    @SendToUser("/queue/ack")
    public String handleMessage(MessageEntity message, SimpMessageHeaderAccessor headers) {
        String token = (String) headers.getSessionAttributes().get("jwt"); // Получаем jwt

        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("JWT токен не валиден");
        }

        String senderUsername = jwtUtils.getUsernameFromToken(token); // Получаем имя отправителя
        message.setSender(senderUsername);
        message.setTimestamp(Instant.now().toString());

        messageService.createMessage(message);

        simpMessagingTemplate.convertAndSendToUser(
                message.getRecipient(),
                "/queue/messages",
                message
        );

        return "Message sent to recipient";
    }

    @MessageMapping("/fetchMessages")
    @SendToUser("/queue/messages")
    public List<MessageEntity> fetchMessages(String jwtToken, Principal principal) {
        if (!jwtUtils.validateToken(jwtToken)) {
            throw new RuntimeException("JWT токен не валиден");
        }



        String username = jwtUtils.getUsernameFromToken(jwtToken);

        // Получаем непрочитанные сообщения пользователя из сервиса
        List<MessageEntity> messages = messageService.getMessages(username);


        return messages;
    }


}

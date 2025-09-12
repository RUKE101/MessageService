package ru.afonskiy.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.afonskiy.messenger.entity.MessageEntity;
import ru.afonskiy.messenger.service.MessageService;
import ru.afonskiy.messenger.jwt.util.JwtUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class DMChatWsController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtUtils jwtUtils;

    @MessageMapping("/chat")
    @SendToUser("/queue/ack")
    public String handleMessage(MessageEntity message, SimpMessageHeaderAccessor headers) {
        String token = (String) Objects.requireNonNull(headers.getSessionAttributes()).get("jwt");
        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("JWT токен не валиден");
        }
        String senderUsername = jwtUtils.getUsernameFromToken(token);

        message.setSender(senderUsername);
        message.setTimestamp(Instant.now().toString());
        simpMessagingTemplate.convertAndSendToUser(
                message.getRecipient(),
                "/queue/messages",
                message
        );

        messageService.createMessage(message);
        return "Message sent to recipient";
    }

    @MessageMapping("/fetchUnreadMessages")
    @SendToUser("/queue/messages")
    public List<MessageEntity> fetchUnreadMessages(@Header("authorization") String jwtToken) {
        if (!jwtUtils.validateToken(jwtToken)) {
            throw new RuntimeException("JWT токен не валиден");
        }

        String username = jwtUtils.getUsernameFromToken(jwtToken);

        return messageService.getUnreadMessages(username);
    }

    @MessageMapping("/fetchUserMessages")
    @SendToUser("/queue/messages")
    public List<MessageEntity> fetchMessagesFromUser(@Header("authorization") String jwtToken,
                                                     @Header("sender") String sender) {
        if (!jwtUtils.validateToken(jwtToken)) {
            throw new RuntimeException("JWT токен не валиден");
        }

        String username = jwtUtils.getUsernameFromToken(jwtToken);
        return messageService.getMessages(username, sender.substring(1));
    }

    @MessageMapping("/deleteMessage")
    @SendToUser("/queue/ack")
    public String deleteMessage(@Header("authorization") String jwtToken, @Header("id") String messageId) {
        if (!jwtUtils.validateToken(jwtToken)) {
            throw new RuntimeException("JWT токен не валиден");
        }

        messageService.deleteMessage(messageId);
        return "Message deleted successfully";
    }

    @MessageMapping("/changeMessage")
    @SendToUser("/queue/ack")
    public String changeMessage(@Header("authorization") String jwtToken, @Header("messageId") String messageId,
                                @Header("text") String newText) {
        jwtToken = jwtToken.trim();
        if (!jwtUtils.validateToken(jwtToken)) {
            throw new RuntimeException("JWT токен не валиден");
        }

        String username = jwtUtils.getUsernameFromToken(jwtToken);
        messageService.updateMessage(messageId, newText, username);
        return "Message updated successfully";
    }




}

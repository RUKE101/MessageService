package ru.afonskiy.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.afonskiy.messenger.entity.GroupMessageEntity;
import ru.afonskiy.messenger.jwt.util.JwtUtils;
import ru.afonskiy.messenger.service.GroupMessageService;

import java.time.Instant;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class GroupChatWsController {
    private final GroupMessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtUtils jwtUtils;


    @MessageMapping("/group")
    @SendToUser("/queue/ack")
    public String sendMessage(GroupMessageEntity message, SimpMessageHeaderAccessor headers) {
        String token = (String) Objects.requireNonNull(headers.getSessionAttributes()).get("jwt");
        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("JWT токен не валиден");
        }
        String senderUsername = jwtUtils.getUsernameFromToken(token);
        String groupId = message.getGroupId();

        message.setSender(senderUsername);
        message.setTimestamp(Instant.now().toString());
        simpMessagingTemplate.convertAndSend("/topic/group/" + groupId, message);

        messageService.sendMessage(message, token);
        return "Message sent to group";
    }
}

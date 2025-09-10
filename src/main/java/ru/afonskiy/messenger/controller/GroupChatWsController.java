package ru.afonskiy.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.afonskiy.messenger.entity.GroupMessageEntity;
import ru.afonskiy.messenger.jwt.util.JwtUtils;
import ru.afonskiy.messenger.service.GroupMessageService;
import ru.afonskiy.messenger.service.GroupService;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class GroupChatWsController {
    private final GroupMessageService messageService;
    private final GroupService groupService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtUtils jwtUtils;


    @MessageMapping("/group/{groupId}/send")
    @SendToUser("/queue/ack")
    public String sendMessage(@DestinationVariable String groupId, GroupMessageEntity message,
                              SimpMessageHeaderAccessor headers) {
        String token = (String) Objects.requireNonNull(headers.getSessionAttributes()).get("jwt");
        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("JWT токен не валиден");
        }
        String senderUsername = jwtUtils.getUsernameFromToken(token);
        message.setGroupId(groupId);
        message.setSender(senderUsername);
        message.setTimestamp(Instant.now().toString());
        simpMessagingTemplate.convertAndSend("/topic/group/" + groupId, message);

        messageService.sendMessage(message, token);
        return "Message sent to group";
    }

    @MessageMapping("/group/{groupId}/fetch")
    @SendToUser("/queue/messages")
    public List<GroupMessageEntity> fetchMessagesFromGroup(@DestinationVariable String groupId,
                                                            SimpMessageHeaderAccessor headers) {
        String token = (String) Objects.requireNonNull(headers.getSessionAttributes()).get("jwt");
        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("JWT токен не валиден");
        }
        String uuid = jwtUtils.getCurrentUIID(token);

        return messageService.getMessagesFromGroup(uuid, groupId);
    }

    @MessageMapping("/group/{groupId}/update")
    @SendToUser("/queue/ack")
    public String updateMessageFromGroup(@DestinationVariable String groupId, @Header("messageId") String messageId,
                                         @Header("text") String newText,
                                         SimpMessageHeaderAccessor headers) {
        String token = (String) Objects.requireNonNull(headers.getSessionAttributes()).get("jwt");
        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("JWT токен не валиден");
        }
        String userUuid = jwtUtils.getCurrentUIID(token);
        String username = jwtUtils.getUsernameFromToken(token);
        messageService.updateGroupMessage(messageId, groupId, userUuid, username, newText);
        return "Message updated successfully";
    }

    @MessageMapping("/group/{groupId}/delete")
    @SendToUser("/queue/messages")
    public String deleteMessageFromGroup(@DestinationVariable String groupId,
                                         @Header("messageId") String messageId,
                                                           SimpMessageHeaderAccessor headers) {
        String token = (String) Objects.requireNonNull(headers.getSessionAttributes()).get("jwt");
        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("JWT токен не валиден");
        }
        String userUuid = jwtUtils.getCurrentUIID(token);
        String username = jwtUtils.getUsernameFromToken(token);
        messageService.deleteGroupMessage(messageId, groupId, userUuid, username);
        return "Message deleted successfully";
    }
}

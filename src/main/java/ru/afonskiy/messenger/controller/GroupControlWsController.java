package ru.afonskiy.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.afonskiy.messenger.entity.GroupEntity;
import ru.afonskiy.messenger.jwt.util.JwtUtils;
import ru.afonskiy.messenger.service.GroupService;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class GroupControlWsController {
    private final GroupService groupService;
    private final JwtUtils jwtUtils;
    @MessageMapping("/group/preferences")
    @SendToUser("/queue/ack")
    public String createGroup(GroupEntity groupEntity, SimpMessageHeaderAccessor headers) {
        String token = (String) Objects.requireNonNull(headers.getSessionAttributes()).get("jwt");
        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("JWT токен не валиден");
        }
        groupEntity.setOwnerId(jwtUtils.getCurrentUIID());
        groupService.createGroup(groupEntity, token);


        return "Group created successfully";
    }
}

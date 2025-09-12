package ru.afonskiy.messenger.controller.group;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import ru.afonskiy.messenger.entity.group.GroupEntity;
import ru.afonskiy.messenger.jwt.util.JwtUtils;
import ru.afonskiy.messenger.service.group.GroupService;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Component("GroupsControlWsController")
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
        groupEntity.setOwnerId(jwtUtils.getCurrentUIID(token));
        groupService.createGroup(groupEntity, token);


        return "Group created successfully";
    }

    @MessageMapping("/group/view")
    @SendToUser("/queue/ack")
    public List<GroupEntity> viewGroups(SimpMessageHeaderAccessor headers) {
        String token = (String) Objects.requireNonNull(headers.getSessionAttributes()).get("jwt");
        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("JWT токен не валиден");
        }
        String uuid = jwtUtils.getCurrentUIID(token);
        return groupService.getUserGroups(uuid);
    }
}

package ru.afonskiy.messenger.controller.invitation;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ru.afonskiy.messenger.entity.invitation.Invite;
import ru.afonskiy.messenger.jwt.util.JwtUtils;
import ru.afonskiy.messenger.service.invitation.InviteService;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class InviteGroupWsController {
    private final InviteService inviteService;
    private final JwtUtils jwtUtils;

    @MessageMapping("/invite/create")
    @SendToUser("/queue/ack")
    public String createInvite(Invite invite, SimpMessageHeaderAccessor headers) {
        String token = (String) Objects.requireNonNull(headers.getSessionAttributes()).get("jwt");
        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("JWT token is not valid");
        }
        inviteService.createInvite(invite);
        return "Invite created successfully";
    }

    @MessageMapping("/invite/join")
    @SendToUser("/queue/ack")
    public String joinByInvite(String inviteUuid, SimpMessageHeaderAccessor headers) {
        String token = (String) Objects.requireNonNull(headers.getSessionAttributes()).get("jwt");
        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("JWT token is not valid");
        }
        String userUuid = jwtUtils.getCurrentUIID(token);
        boolean joined = inviteService.joinByInvite(inviteUuid, userUuid);
        return joined ? "Joined group by invite successfully" : "Failed to join group by invite";
    }
}
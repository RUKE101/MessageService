package ru.afonskiy.messenger.service.invitation;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.afonskiy.messenger.entity.group.GroupEntity;
import ru.afonskiy.messenger.entity.invitation.Invite;
import ru.afonskiy.messenger.repository.invitation.InviteRepository;
import ru.afonskiy.messenger.service.group.GroupService;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InviteService {
    private final GroupService groupService;
    private final InviteRepository repository;


    public Invite createInvite(Invite inviteEntity) {
        GroupEntity group = groupService.getGroupById(inviteEntity.getGroupId());
        if (group == null) {
            throw new NotFoundException("Group not found. Group id: " + inviteEntity.getGroupId());
        }
        return repository.save(inviteEntity);
    }

    public Invite getInviteByUuid(String inviteUuid) {
        Optional<Invite> invite = repository.findById(inviteUuid);
        if (invite.isEmpty()) {
            throw new NotFoundException("Not found invite with this id: " + inviteUuid);
        }
        return invite.get();
    }

    public boolean joinByInvite(String inviteUuid, String userUuid) {
        Invite invite = getInviteByUuid(inviteUuid);
        if (groupService.isUserInGroup(userUuid, invite.getGroupId())) {
            throw new AccessDeniedException("You already participate this group");
        }
        if (invite.getIsCountable().equals(false)) {
            groupService.joinGroup(userUuid, invite.getGroupId());
            return true;
        }
        if (invite.getJoinPlaces() <= 0) {
            throw new RuntimeException("No places for this invite left");
        }
        groupService.joinGroup(userUuid, invite.getGroupId());
        invite.setJoinPlaces(invite.getJoinPlaces() - 1);
        return true;
    }

}

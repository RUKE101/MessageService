package ru.afonskiy.messenger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.afonskiy.messenger.entity.GroupMemberEntity;
import ru.afonskiy.messenger.entity.GroupRoles;
import ru.afonskiy.messenger.jwt.util.JwtUtils;
import ru.afonskiy.messenger.repository.GroupMemberRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;
    private final JwtUtils principalService;
    private final SendLogsService sendLogsService;

    public GroupMemberEntity createGroupMember(String groupId,String token) {
        String userUuid = principalService.getCurrentUIID(token);
        try {
            GroupMemberEntity member = groupMemberRepository.findByUuidOfUser(userUuid);
            if (member == null) {
                member = new GroupMemberEntity();
                member.setUuidOfUser(userUuid);
                member.setGroupId(new ArrayList<>());
                member.setGroupRoles(new ArrayList<>());
            }
            if (member.getGroupId() == null) {
                member.setGroupId(new ArrayList<>());
            }
            if (!member.getGroupId().contains(groupId)) {
                member.getGroupId().add(groupId);
            }
            if (member.getGroupRoles() == null) {
                member.setGroupRoles(new ArrayList<>());
            }
            boolean hasGroupRole = member.getGroupRoles().stream()
                    .anyMatch(gr -> gr.getGroupId().equals(groupId));
            if (!hasGroupRole) {
                GroupRoles groupRole = new GroupRoles();
                groupRole.setGroupId(groupId);
                groupRole.setRoles(List.of("user"));
                member.getGroupRoles().add(groupRole);
            }
            return groupMemberRepository.save(member);

        } catch (Exception e) {
            sendLogsService.sendLogs("createGroupMember", e.getMessage(), token);
            throw new RuntimeException("Failed to create group member", e);
        }
    }
}

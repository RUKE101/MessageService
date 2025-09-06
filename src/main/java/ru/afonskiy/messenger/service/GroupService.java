package ru.afonskiy.messenger.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.afonskiy.messenger.entity.GroupEntity;
import ru.afonskiy.messenger.entity.GroupMemberEntity;
import ru.afonskiy.messenger.repository.GroupMemberRepository;
import ru.afonskiy.messenger.repository.GroupRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final SendLogsService sendLogsService;
    private final GetCurrentUIID getCurrentUIID;

    public GroupEntity getGroupById(String groupId) {
        Optional<GroupEntity> group = groupRepository.findById(groupId);
        if (group.isEmpty()) {
            throw new NotFoundException("Not found group with this id: " + groupId);
        }
        return group.get();
    }

    public GroupEntity createGroup(GroupEntity groupEntity,String token) {
        try {
            groupRepository.save(groupEntity);
            GroupMemberEntity groupMemberEntity = new GroupMemberEntity();
            return groupEntity;
        } catch (Exception e) {
            sendLogsService.sendLogs("Create group failed", token, e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public List<String> findAllGroupsForUser(String uuid, String token) {
        try {
            GroupMemberEntity memberEntity = groupMemberRepository.findByUuidOfUser(uuid);
            if (memberEntity == null || memberEntity.getGroupId() == null) {
                return List.of();
            }
            List<String> groupIds = memberEntity.getGroupId().stream()
                    .distinct()
                    .toList();
            List<GroupEntity> groupEntities = groupRepository.findByGroupIdIn(groupIds);
            return groupEntities.stream()
                    .map(GroupEntity::getNameOfGroup)
                    .toList();
        } catch (Exception e) {
            sendLogsService.sendLogs("Find all groups for user failed", e.getMessage(), token);
            throw new RuntimeException(e);
        }
    }
    public GroupEntity updateGroup( String id,String  nameOfGroup,String descriptionOfGroup ,String token) {
        try{
            GroupEntity groupEntity = groupRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("Group id not found")
            );
            groupEntity.setNameOfGroup(nameOfGroup);
            groupEntity.setDescriptionOfGroup(descriptionOfGroup);
            groupRepository.save(groupEntity);
            return groupEntity;
        } catch (Exception e){
            sendLogsService.sendLogs("Update group failed", e.getMessage(), token);
            throw new RuntimeException(e);
        }
    }
    public void deleteGroup(String id,String  token) {
        try {
            groupMemberRepository.deleteById(id);
        } catch (Exception e){
            sendLogsService.sendLogs("Delete group failed", e.getMessage(), token);
            throw new RuntimeException(e);
        }
    }

}
